package com.core.utils;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;


/**
 * description
 *
 * @author LSZ 2020/07/25 18:04
 * @contact 648748030@qq.com
 */
public class ApplicationUtils {

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        ApplicationUtils.context = context;
    }


    public static Class getServiceClass(Class clazz) {
        Class entityClass = (Class) ((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        return entityClass;
    }

}
