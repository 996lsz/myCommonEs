package com.core.config;

import com.core.aop.DynamicDataSourceAnnotationAdvisor;
import com.core.aop.DynamicDataSourceAnnotationInterceptor;
import com.core.entity.config.DynamicDataSourceProperties;
import com.core.provider.DynamicDataSourceProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

/**
 * description
 * 
 * @author LSZ 2021/03/01 16:41
 * @contact 648748030@qq.com
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DynamicDataSourceAutoConfiguration {

	@Autowired
	private DynamicDataSourceProperties properties;

	@Bean("dynamicDataSourceMap")
	public Map<String, RestClient> dynamicDataSourceMap() {
		DynamicDataSourceProvider dynamicDataSourceProvider = new DynamicDataSourceProvider(properties);
		Map<String, RestClient> stringRestClientMap = dynamicDataSourceProvider.loadDataSources();
		return stringRestClientMap;
	}

	@Bean
	@ConditionalOnMissingBean
	public DynamicDataSourceAnnotationAdvisor dynamicDatasourceAnnotationAdvisor() {
		DynamicDataSourceAnnotationInterceptor interceptor = new DynamicDataSourceAnnotationInterceptor();
		DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(interceptor);
		return advisor;
	}


}
