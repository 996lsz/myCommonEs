package com.core.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfiguration {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Value("${elasticsearch.hostname}")
	private String hostName;

	@Value("${elasticsearch.username}")
	private String userName;

	@Value("${elasticsearch.password}")
	private String password;

	@Value("${elasticsearch.port}")
	private Integer port;

	/**
	 * 注册Elasticsearch
	 */
	@Bean
	public RestClient restClient() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
		return RestClient.builder(new HttpHost(hostName, port, "http"))
				.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
				.setRequestConfigCallback(requestConfigBuilder -> {
					requestConfigBuilder.setConnectTimeout(3000);
					requestConfigBuilder.setSocketTimeout(3000);
					requestConfigBuilder.setConnectionRequestTimeout(3000);
					return requestConfigBuilder;
				}).build();
	}


}
