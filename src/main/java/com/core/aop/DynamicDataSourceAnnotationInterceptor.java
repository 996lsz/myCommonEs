package com.core.aop;

import com.core.annotation.DS;
import com.core.service.DynamicDataSourceContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * description
 * 
 * @author LSZ 2021/03/01 15:46
 * @contact 648748030@qq.com
 */
public class DynamicDataSourceAnnotationInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			DynamicDataSourceContextHolder.setDataSourceLookupKey(determineDatasource(invocation));
			return invocation.proceed();
		} finally {
			DynamicDataSourceContextHolder.clearDataSourceLookupKey();
		}
	}

	private String determineDatasource(MethodInvocation invocation) {
		Method method = invocation.getMethod();
		Class<?> declaringClass = invocation.getThis().getClass();
		DS ds = method.isAnnotationPresent(DS.class) ? method.getAnnotation(DS.class)
				: AnnotationUtils.findAnnotation(declaringClass, DS.class);
		String key = ds.value();
		return key;
	}

}
