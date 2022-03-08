package com.nico.dispatch;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nico.common.BeanWapper;
import com.nico.common.RespWapper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
public class Dispatcher {


    @Autowired
    private HandlerMapping handlerMapping;


    public RespWapper dispatch(FullHttpRequest req) {

        String fullUri = req.uri();
        String uri = fullUri.split("\\?")[0];


        HttpMethod reqMethod = req.method();
        String content = req.content().toString(CharsetUtil.UTF_8);

        BeanWapper beanWapper = handlerMapping.get(uri);
        if (beanWapper == null) {
            return new RespWapper(HttpResponseStatus.NOT_FOUND);
        }
        Object bean = beanWapper.getBean();
        Method method = beanWapper.getMethod();
        Class paramType = beanWapper.getParamType();
        try {
            Object ret = null;
            if (paramType == null) {
                ret = method.invoke(bean, null);
            } else {
                Object reqBody = JSON.parseObject(content, paramType);
                ret = method.invoke(bean, reqBody);
            }
            if (ret == null) {
                return new RespWapper(HttpResponseStatus.OK);
            }
            return new RespWapper(JSON.toJSONString(ret, SerializerFeature.DisableCircularReferenceDetect), HttpResponseStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new RespWapper(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        //return new RespWapper(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
}
