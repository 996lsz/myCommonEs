package com.core.annotation.registarar;

import com.core.annotation.PoScan;
import com.core.entity.config.DataSourceProperty;
import com.core.utils.EsConfigHelper;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 配置初始化
 * 
 * @author LSZ 2021/02/22 10:44
 * @contact 648748030@qq.com
 */
public class PoScannerRegistrar implements ImportBeanDefinitionRegistrar , EnvironmentAware {

	private Environment environment;

	private Binder binder;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(PoScan.class.getName()));
		//获取包扫描路径
		String[] values = annoAttrs.getStringArray("value");
		//获取ES相关配置
		DataSourceProperty commonEsConfig = binder.bind("elasticsearch.datasource.dynamic.datasource.master", Bindable.of(DataSourceProperty.class)).get(); //获取所有数据源配置

		EsConfigHelper.setCommonEsConfig(commonEsConfig);
		EsConfigHelper.setScanPackage(values);
	}


	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		binder = Binder.get(environment);
	}
}
