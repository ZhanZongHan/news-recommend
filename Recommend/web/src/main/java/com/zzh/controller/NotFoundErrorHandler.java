package com.zzh.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class NotFoundErrorHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView noHandleFoundHandler(
            HttpServletRequest request,
            Exception e
    ) {
        return new ModelAndView("/error");
    }
}
