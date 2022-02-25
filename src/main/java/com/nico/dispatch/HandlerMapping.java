package com.nico.dispatch;

import com.nico.annotation.Router;
import com.nico.model.ProxyWapper;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@Slf4j
public class HandlerMapping implements CommandLineRunner {

    @Autowired
    private ConfigurableApplicationContext ctx;
    private static final Map<String, ProxyWapper> continer = new HashMap<>();



    public void handlerMapping() {
        try {
            String[] beanNamesForAnnotation = ctx.getBeanNamesForAnnotation(Controller.class);

            for (String s : beanNamesForAnnotation) {
                Object bean = ctx.getBean(s);
                Class<?> clazz = bean.getClass();
                Controller annotation = clazz.getAnnotation(Controller.class);
                if (annotation == null) {
                    break;
                }
                String controllerValue = annotation.value();
                log.info("anntition value = {}", controllerValue);
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    Annotation[] anns = method.getAnnotations();
                    for (Annotation ann : anns) {
                        if (ann.annotationType().getName().equals(Router.class.getName())) {
                            Router router = (Router) ann;
                            String name = router.name();
                            log.info("key = {}",controllerValue + name);
                            continer.put(controllerValue + name, new ProxyWapper(bean,method));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }

    }


    private Object proxy(Object bean,Method method,Object args){
        Object invoke = null;
        try {
            invoke = method.invoke(bean, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoke;
    }

    public ProxyWapper get(String uri) {
        return continer.get(uri);
    }

    @Override
    public void run(String... args) throws Exception {
        handlerMapping();
    }
}
