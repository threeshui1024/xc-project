package com.xc.base.execption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理XcException异常，此类异常是程序员主动抛出的，可预知异常
     * @param e
     * @return
     */
    @ExceptionHandler(XcException.class)//此方法捕获XcException异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500，默认就是500，可写可不写
    @ResponseBody//将信息返回为json格式
    public RestErrorResponse doXcException(XcException e){
        String errMessage = e.getErrMessage();
        log.error("捕获异常：{}", errMessage);

        return new RestErrorResponse(errMessage);
    }

    /**
     * 捕获不可预知异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)//此方法捕获Exception异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500，默认就是500，可写可不写
    @ResponseBody//将信息返回为json格式
    public RestErrorResponse doException(Exception e){
        String message = e.getMessage();
        log.error("捕获异常：{}", message);

        return new RestErrorResponse(message);
    }

    /**
     * 捕获JSR303校验的MethodArgumentNotValidException异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)//此方法捕获MethodArgumentNotValidException异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500，默认就是500，可写可不写
    @ResponseBody//将信息返回为json格式
    public RestErrorResponse doMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuffer stringBuffer = new StringBuffer();//用来保存异常信息
        fieldErrors.forEach(error -> {
            stringBuffer.append(error.getDefaultMessage()).append(",");
        });

        return new RestErrorResponse(stringBuffer.toString());
    }

}
