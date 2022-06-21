package com.xjc.aop.result;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler({BindException.class})
    public ResultVo MethodArgumentNotValidExceptionHandler(BindException e) {
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        return new ResultVo(ResultCodeEnum.VALIDATE_ERROR, objectError.getDefaultMessage());
    }

    @ExceptionHandler({Exception.class})
    public ResultVo MethodArgumentFailedExceptionHandler(Exception e){
        return new ResultVo(ResultCodeEnum.FAILED, e.getMessage());
    }
}
