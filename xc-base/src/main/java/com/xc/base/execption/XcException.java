package com.xc.base.execption;

/**
 * 学成在线项目异常类
 */
public class XcException extends RuntimeException{
    private String errMessage; //异常信息

    public XcException() {
    }

    public XcException(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(String message){
        throw new XcException(message);
    }

    public static void cast(CommonError commonError){
        throw new XcException(commonError.getErrMessage());
    }
}
