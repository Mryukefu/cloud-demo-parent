package com.example.clouddemocommon.exception;

import lombok.Getter;

/**
 * @author yukefu
 * @Title: BusinessException
 * @ProjectName frameworkdemo
 *  自定义业务异常
 * @date 2019/8/12 18:10
 */
@Getter
public class BusinessException extends RuntimeException {
    /**
     * 错误代码
     */
    protected int errCode;

    public BusinessException(int code) {
        super();
        this.errCode = code;
    }

    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String msg, int errCode) {
        super(msg);
        this.errCode = errCode;
    }

    public BusinessException(String msg, Throwable ex) {
        super(msg, ex);
    }

}