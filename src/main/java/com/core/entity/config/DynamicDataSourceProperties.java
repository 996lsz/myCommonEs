package com.core.entity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * description
 * 
 * @author LSZ 2021/03/03 10:26
 * @contact 648748030@qq.com
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "elasticsearch.datasource.dynamic")
public class DynamicDataSourceProperties {

	/**
	 * 必须设置默认的库,默认master
	 */
	private String primary = "master";
	/**
	 * 每一个数据源
	 */
	private Map<String, DataSourceProperty> datasource = new LinkedHashMap<>();
	/**
	 * aop切面顺序，默认优先级最高
	 */
	private Integer order = Ordered.HIGHEST_PRECEDENCE;
}
