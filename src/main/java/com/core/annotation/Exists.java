package com.core.annotation;

import com.core.constant.EsBaseAnnotationConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Exists {

	String fieldName() default "";

	EsBaseAnnotationConstant.BoolTypeEnum bool() default EsBaseAnnotationConstant.BoolTypeEnum.MUST;

}
