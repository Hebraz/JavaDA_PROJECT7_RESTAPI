package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * ErrorController : handles exception
 */
@ControllerAdvice
@Log4j2
public class ErrorController {

    /**
     * Handles general exception and shows error page
     * @param ex exception raised
     * @param request http request
     * @param redirectAttributes
     * @param model view model
     * @return error page
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralExceptions(
            Exception ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.error(ex.getMessage());
        model.addAttribute("error",ex.getMessage());
        return "/error";
    }
}
