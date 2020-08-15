package com.core.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.map.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * description
 * 
 * @author LSZ 2020/07/22 17:20
 * @contact 648748030@qq.com
 */
@Data
public class IndexProperty {

	//保存对象@Index数据
	private String indexName;

	//key 注解.class,  value字段及get方法
	private MultiValueMap propertyMap;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Property{

		private String fieldName;

		private Method getMethod;

		private Annotation annotation;


	}
}
