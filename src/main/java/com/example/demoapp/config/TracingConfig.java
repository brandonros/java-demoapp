package com.example.demoapp.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.tracing.Tracer;
import net.ttddyy.observation.boot.autoconfigure.DataSourceObservationAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfiguration(before = DataSourceObservationAutoConfiguration.class)
public class TracingConfig {

    @Bean
    @ConditionalOnClass(name = "io.micrometer.observation.aop.ObservedAspect")
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    @Bean
    public CustomTraceIdProvider customTraceIdProvider(Tracer tracer) {
        return new CustomTraceIdProvider(tracer);
    }

    public static class CustomTraceIdProvider {
        private final Tracer tracer;

        public CustomTraceIdProvider(Tracer tracer) {
            this.tracer = tracer;
        }

        public String getTraceId() {
            var span = tracer.currentSpan();
            return span != null ? span.context().traceId() : "no-trace";
        }
    }
}