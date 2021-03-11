package com.core.provider;

import com.core.entity.config.DataSourceProperty;
import com.core.entity.config.DynamicDataSourceProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * description
 * 
 * @author LSZ 2021/03/03 11:23
 * @contact 648748030@qq.com
 */
public class DynamicDataSourceProvider {
	/**
	 * 多数据源参数
	 */
	private DynamicDataSourceProperties properties;

	public DynamicDataSourceProvider(DynamicDataSourceProperties properties) {
		this.properties = properties;
	}

	public Map<String, RestClient> loadDataSources() {
		Map<String, DataSourceProperty> dataSourcePropertiesMap = properties.getDatasource();
		Map<String, RestClient> dataSourceMap = new HashMap<>(dataSourcePropertiesMap.size());
		for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
			String pollName = item.getKey();
			DataSourceProperty dataSourceProperty = item.getValue();
			dataSourceMap.put(pollName, createDataSource(dataSourceProperty));
		}
		return dataSourceMap;
	}

	private RestClient createDataSource(DataSourceProperty dataSourceProperty) {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(dataSourceProperty.getUserName(), dataSourceProperty.getPassword()));
		return RestClient.builder(new HttpHost(dataSourceProperty.getHostName(), dataSourceProperty.getPort(), "http"))
				.setHttpClientConfigCallback(httpClientBuilder -> {
					httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					httpClientBuilder.setMaxConnPerRoute(100);
					httpClientBuilder.setMaxConnTotal(200);
					return httpClientBuilder;
				})
				.setRequestConfigCallback(requestConfigBuilder -> {
					requestConfigBuilder.setConnectTimeout(3000);
					requestConfigBuilder.setSocketTimeout(30000);
					requestConfigBuilder.setConnectionRequestTimeout(3000);
					return requestConfigBuilder;
				}).build();
	}
}
