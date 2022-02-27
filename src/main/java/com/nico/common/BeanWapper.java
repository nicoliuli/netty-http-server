package com.nico.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanWapper {
    private Object bean;
    private Method method;
    private Class paramType;

}
