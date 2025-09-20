package com.example.demoapp.config;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Database configuration with query-level metrics using datasource-proxy.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "primaryDataSourceRaw")
    @ConfigurationProperties("spring.datasource.primary.hikari")
    public DataSource primaryDataSourceRaw() {
        return primaryDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
    }

    @Bean
    @Primary
    public DataSource primaryDataSource(
            @Qualifier("primaryDataSourceRaw") DataSource dataSource,
            MeterRegistry meterRegistry) {

        return ProxyDataSourceBuilder.create(dataSource)
            .name("primary")
            .listener(new QueryMetricsListener(meterRegistry))
            .build();
    }

    @Bean
    @Primary
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Custom query execution listener that records metrics for each query.
     */
    private static class QueryMetricsListener implements QueryExecutionListener {
        private static final Pattern PROC_PATTERN = Pattern.compile("EXEC(?:UTE)?\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        private static final Pattern TBL_PATTERN = Pattern.compile("(?:FROM|INTO|UPDATE)\\s+(\\w+)|DELETE\\s+FROM\\s+(\\w+)", Pattern.CASE_INSENSITIVE);

        private final MeterRegistry meterRegistry;

        public QueryMetricsListener(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }

        @Override
        public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            // Not needed for metrics
        }

        @Override
        public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            for (QueryInfo queryInfo : queryInfoList) {
                String query = queryInfo.getQuery();
                String queryName = extractQueryName(query);
                String queryType = extractQueryType(query);

                // Record query execution time with tags
                meterRegistry.timer("jdbc.query.execution",
                        "name", queryName,
                        "type", queryType,
                        "datasource", execInfo.getDataSourceName())
                    .record(execInfo.getElapsedTime(), TimeUnit.MILLISECONDS);

                // Log for debugging
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Query metrics recorded - name: {}, type: {}, time: {}ms",
                        queryName, queryType, execInfo.getElapsedTime());
                }
            }
        }

        private String extractQueryName(String query) {
            if (query == null) return "unknown";
            String trimmed = query.trim();

            // Check for stored procedure
            Matcher procMatcher = PROC_PATTERN.matcher(trimmed);
            if (procMatcher.find()) {
                return procMatcher.group(1).toLowerCase();
            }

            // Check for table operations
            Matcher tableMatcher = TBL_PATTERN.matcher(trimmed);
            if (tableMatcher.find()) {
                String table = tableMatcher.group(1) != null ? tableMatcher.group(1) : tableMatcher.group(2);
                if (table != null) {
                    return table.toLowerCase();
                }
            }

            return "unknown";
        }

        private String extractQueryType(String query) {
            if (query == null) return "unknown";
            String trimmed = query.trim().toUpperCase();

            if (trimmed.startsWith("EXEC")) return "stored_procedure";
            if (trimmed.startsWith("SELECT")) return "select";
            if (trimmed.startsWith("INSERT")) return "insert";
            if (trimmed.startsWith("UPDATE")) return "update";
            if (trimmed.startsWith("DELETE")) return "delete";
            if (trimmed.startsWith("CREATE") || trimmed.startsWith("ALTER") || trimmed.startsWith("DROP")) {
                return "ddl";
            }

            return "other";
        }
    }
}