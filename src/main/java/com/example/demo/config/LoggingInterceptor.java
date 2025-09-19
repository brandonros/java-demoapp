package com.example.demo.config;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";
    private static final String START_TIME_ATTRIBUTE = "startTime";

    private final Tracer tracer;

    public LoggingInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate or extract correlation ID
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add to MDC for logging
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

        // Add to response header
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        // Add OpenTelemetry trace ID to response and tag span with correlation ID
        var span = tracer.currentSpan();
        if (span != null) {
            String traceId = span.context().traceId();
            response.setHeader(TRACE_ID_HEADER, traceId);

            // Add correlation ID as a span tag for Jaeger search
            span.tag("correlation.id", correlationId);
            span.tag("http.request.method", request.getMethod());
            span.tag("http.request.uri", request.getRequestURI());
        }

        // Store start time for duration calculation
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        // Log incoming request
        LOGGER.info("Incoming request: {} {} from {}",
            request.getMethod(),
            request.getRequestURI(),
            request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // Calculate request duration
        long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long duration = System.currentTimeMillis() - startTime;

        // Log response
        if (ex != null) {
            LOGGER.error("Request completed with error: {} {} - Status: {} - Duration: {}ms - Error: {}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration,
                ex.getMessage());
        } else {
            LOGGER.info("Request completed: {} {} - Status: {} - Duration: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);
        }

        // Clear MDC
        MDC.remove(CORRELATION_ID_MDC_KEY);
    }
}