package com.nico.dispatch;


import com.nico.model.ProxyWapper;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
@Slf4j
public class Dispatcher {


    @Autowired
    private HandlerMapping handlerMapping;


    public void dispatch(FullHttpRequest req) {

        String fullUri = req.uri();
        String[] split = fullUri.split("\\?");
        int len = split.length;
        String uri = split[0];
        String paramsStr = "";
        if (len > 1) {
            paramsStr = split[1];
        }
        log.info("uri = {},paramsStr = {}", uri, paramsStr);
        ProxyWapper proxyWapper = handlerMapping.get(uri);
        Object bean = proxyWapper.getBean();
        Method method = proxyWapper.getMethod();
        try {
            Object invoke = method.invoke(bean, 1);
            System.out.println(invoke);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
