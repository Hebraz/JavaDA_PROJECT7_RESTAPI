package com.nnk.springboot.controllers.log;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * Interceptor for request logging
 */
@Component
@Log4j2
public class RequestInterceptor extends HandlerInterceptorAdapter {
    /**
     * Logs request when received by any controllers
     * @param request the request to log
     * @param handler controller from which the request has been intercepted
     * @return true
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        log.info("REQUEST:" + request.getMethod() + " " + request.getRequestURI());
        //log of body in case of POST
        if ("POST".equalsIgnoreCase(request.getMethod()))
        {
            Enumeration<String> params =  request.getParameterNames();
            while ( params.hasMoreElements() ) {
                String param = params.nextElement();
                if(! param.equalsIgnoreCase("_csrf")){
                    log.info(param + ": '" + request.getParameter(param) + "'");
                }
            }
            log.info("");
        }
        return true;
    }

    /**
     * Logs of response status
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
            log.info("RESPONSE: " + response.getStatus());
    }
}
