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

@ControllerAdvice
@Log4j2
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public String handleGeneralExceptions(
            Exception ex,
            HttpServletRequest request,
            Model model ) {

        log.error(ex.getStackTrace());
        model.addAttribute("errorMsg",ex.getMessage());
        model.addAttribute("backUrl", "/bidList/list");
        return "error";
    }
}
