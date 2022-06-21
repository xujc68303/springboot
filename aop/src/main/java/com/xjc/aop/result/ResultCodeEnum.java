package com.xjc.aop.result;

public enum ResultCodeEnum implements StatusCode {

    SUCCESS(1000, "请求成功"),

    FAILED(1001, "系统繁忙"),

    VALIDATE_ERROR(1002, "参数校验失败"),

    RESPONSE_PACK_ERROR(1003, "response返回包装失败");

    private final int code;

    private final String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
