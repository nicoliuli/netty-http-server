package com.nico.dispatch;

import com.nico.annotation.Router;
import com.nico.common.BeanWapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class HandlerMapping implements CommandLineRunner {

    @Autowired
    private ConfigurableApplicationContext ctx;
    @Value("${server.servlet.context-path}")
    private String path;
    private static final Map<String, BeanWapper> continer = new HashMap<>();


    public void handlerMapping() {
        try {
            String[] beanNames = ctx.getBeanNamesForAnnotation(Controller.class);

            for (String beanName : beanNames) {
                Object bean = ctx.getBean(beanName);
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
                            String url = path + controllerValue + name;
                            log.info("url = {}", url);
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            if (parameterTypes.length > 0) {
                                continer.put(url, new BeanWapper(bean, method, parameterTypes[0]));
                            } else {
                                continer.put(url, new BeanWapper(bean, method, null));
                            }

                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public BeanWapper get(String uri) {
        return continer.get(uri);
    }

    @Override
    public void run(String... args) throws Exception {
        handlerMapping();
    }
}
