package com.zhukai.spring.integration.common.response;

/**
 * Created by zhukai on 16-11-29.
 */
public class ResponseBuilder {
    public static <T> Response<T> build(ResponseCodeEnums codeEnum, T body) {
        if (body == null) {
            return build(codeEnum);
        }
        return new Response(codeEnum.getCode(), codeEnum.getMessage(), body);
    }

    public static <T> Response<T> build(ResponseCodeEnums codeEnum) {
        return new Response(codeEnum.getCode(), codeEnum.getMessage());
    }
}
