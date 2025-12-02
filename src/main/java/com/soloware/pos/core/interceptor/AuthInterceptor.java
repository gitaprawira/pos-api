package com.soloware.pos.core.interceptor;

import com.soloware.pos.core.annotation.AuthCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Authentication Interceptor that validates authentication for methods annotated with @AuthCheck.
 * This provides an additional layer of authentication verification at the controller level.
 * 
 * @author Soloware Labs
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Pre-handle method that checks authentication before the controller method is executed.
     * 
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @param handler Chosen handler to execute
     * @return true if the execution should proceed, false to abort
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Only check if the handler is a HandlerMethod (controller method)
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // Check if the method is annotated with @AuthCheck
        AuthCheck authCheck = handlerMethod.getMethodAnnotation(AuthCheck.class);
        
        // If no @AuthCheck annotation, allow the request to proceed
        if (authCheck == null) {
            return true;
        }

        // Get the authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            
            log.warn("Unauthorized access attempt to {} by unauthenticated user", 
                    request.getRequestURI());
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            
            try {
                response.getWriter().write(
                    "{\"statusCode\":401,\"message\":\"Authentication required\",\"data\":null}"
                );
            } catch (Exception e) {
                log.error("Error writing unauthorized response", e);
            }
            
            return false;
        }

        // Log successful authentication check
        log.debug("Authentication check passed for user: {} on endpoint: {}", 
                authentication.getName(), request.getRequestURI());

        return true;
    }

    /**
     * After completion method - can be used for cleanup or logging.
     * 
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @param handler Handler that was executed
     * @param ex Exception thrown during handler execution, if any
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        if (ex != null) {
            log.error("Exception occurred during request processing: {}", ex.getMessage());
        }
    }
}
