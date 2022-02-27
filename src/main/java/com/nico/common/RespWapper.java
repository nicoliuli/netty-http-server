package com.nico.common;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespWapper {
    private String respBody;
    private HttpResponseStatus status;


    public RespWapper(HttpResponseStatus status) {
        this.status = status;
    }
}
