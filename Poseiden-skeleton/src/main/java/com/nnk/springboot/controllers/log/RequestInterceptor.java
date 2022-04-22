package com.nnk.springboot.controllers.log;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Component
@Log4j2
public class RequestInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {

        log.info("REQUEST:" + request.getMethod() + " " + request.getRequestURI());
        if ("POST".equalsIgnoreCase(request.getMethod()))
        {
            Enumeration params =  request.getParameterNames();
            while ( params.hasMoreElements() ) {
                String param = (String)params.nextElement();
                if(! param.equalsIgnoreCase("_csrf")){
                    log.info(param + ": '" + request.getParameter(param) + "'");
                }
            }
            log.info("");
        }
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
            log.info("RESPONSE: " + response.getStatus());
    }
}
