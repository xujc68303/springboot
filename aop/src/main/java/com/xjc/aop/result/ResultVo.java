package com.xjc.aop.result;

import java.io.Serializable;

public class ResultVo implements Serializable {

    private int code;
    private String msg;

    private Object data;

    public ResultVo() {
    }

    public ResultVo(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVo success(Object data) {
        return new ResultVo(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), data);
    }

    public ResultVo fail() {
        return new ResultVo(ResultCodeEnum.FAILED.getCode(), ResultCodeEnum.FAILED.getMsg(), null);
    }

    public ResultVo(StatusCode statusCode, Object data) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
        this.data = data;
    }

    public ResultVo(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
        this.data = null;
    }
}
