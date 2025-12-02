package com.soloware.pos.core.interceptor;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * MDC (Mapped Diagnostic Context) Filter for adding contextual information to logs.
 * This filter enriches logs with request-specific data that will be captured by Logstash.
 * 
 * @author Soloware Labs
 * @since 1.0.0
 */
@Slf4j
@Component
public class MDCFilter implements Filter {

    private static final String REQUEST_ID = "requestId";
    private static final String SESSION_ID = "sessionId";
    private static final String USERNAME = "username";
    private static final String USER_ID = "userId";
    private static final String IP_ADDRESS = "ipAddress";
    private static final String USER_AGENT = "userAgent";
    private static final String ENDPOINT = "endpoint";
    private static final String HTTP_METHOD = "httpMethod";
    private static final String HTTP_STATUS = "httpStatus";
    private static final String DURATION = "duration";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Generate unique request ID
            String requestId = UUID.randomUUID().toString();
            MDC.put(REQUEST_ID, requestId);
            
            // Add request information
            MDC.put(HTTP_METHOD, httpRequest.getMethod());
            MDC.put(ENDPOINT, httpRequest.getRequestURI());
            MDC.put(IP_ADDRESS, getClientIpAddress(httpRequest));
            MDC.put(USER_AGENT, httpRequest.getHeader("User-Agent"));
            
            // Add session ID if available
            if (httpRequest.getSession(false) != null) {
                MDC.put(SESSION_ID, httpRequest.getSession().getId());
            }
            
            // Add user information from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                MDC.put(USERNAME, authentication.getName());
                // You can add user ID here if available from UserEntity
                MDC.put(USER_ID, authentication.getName()); // Or get actual user ID
            }
            
            // Add request ID to response header for tracking
            httpResponse.setHeader("X-Request-ID", requestId);
            
            log.info("Request started: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            
            // Continue the filter chain
            chain.doFilter(request, response);
            
            // Calculate request duration
            long duration = System.currentTimeMillis() - startTime;
            MDC.put(DURATION, String.valueOf(duration));
            MDC.put(HTTP_STATUS, String.valueOf(httpResponse.getStatus()));
            
            log.info("Request completed: {} {} - Status: {} - Duration: {}ms",
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    duration);
            
        } catch (Exception e) {
            log.error("Error processing request: {}", e.getMessage(), e);
            throw e;
        } finally {
            // Always clear MDC to prevent memory leaks
            MDC.clear();
        }
    }
    
    /**
     * Get the real client IP address considering proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("MDC Filter initialized");
    }
    
    @Override
    public void destroy() {
        log.info("MDC Filter destroyed");
    }
}
