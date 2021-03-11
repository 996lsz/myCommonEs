package com.core.config;

import com.core.annotation.*;
import com.core.entity.common.IndexProperty;
import com.core.entity.config.DataSourceProperty;
import com.core.filter.*;
import com.core.utils.ApplicationUtils;
import com.core.utils.EsConfigHelper;
import lombok.Getter;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author LSZ 2020/07/22 17:13
 * @contact 648748030@qq.com
 */
@Configuration
public class EsBaseConfig implements ApplicationListener<ApplicationContextEvent> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	final String RESOURCE_PATTERN = "/**/*.class";

	boolean initFlag = true;

	private final Class[] ES_ANNOTATION = {Term.class, Range.class, Terms.class, Match.class, Id.class, Exists.class, MinimumShouldMatch.class};

	@Getter
	private static Map<Class, IndexProperty> indexMap = new HashMap<>();

	@Getter
	private static Map<Class, String> indexNameMap = new HashMap<>();

	@Getter
	private static Map<Class, Class> serviceGenericityClassMap = new HashMap<>();

	@Getter
	private static List<QueryFilter> filters = new ArrayList<QueryFilter>();

/*	*//**
	 * 注册Elasticsearch
	 *//*
	@Bean("esBaseRestClient")
	public RestClient restClient() {
		DataSourceProperty esConfig = EsConfigHelper.getCommonEsConfig();
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esConfig.getUserName(), esConfig.getPassword()));
		return RestClient.builder(new HttpHost(esConfig.getHostName(), esConfig.getPort(), "http"))
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
	}*/

	@PostConstruct
	public void init(){
		LOGGER.debug("----通用es扫描------");
		String[] scanPackage = EsConfigHelper.getScanPackage();
		for (String pkg : scanPackage) {
			LOGGER.debug("----扫描Po包{}------", pkg);
			try {
				ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
				String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg)
						+ RESOURCE_PATTERN;
				Resource[] resources = resourcePatternResolver.getResources(pattern);
				MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
				for (Resource resource : resources) {
					if (resource.isReadable()) {
						MetadataReader reader = readerFactory.getMetadataReader(resource);
						//扫描到的class
						String className = reader.getClassMetadata().getClassName();
						Class<?> clazz = Class.forName(className);
						//判断是否有Index注解
						Index annotation = clazz.getAnnotation(Index.class);
						if (annotation != null) {
							//这个类使用了自定义注解
							indexMap.put(clazz, getEsIndex(clazz));
						}
					}
				}
			} catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
				LOGGER.error("读取class失败", e);
			}
		}

		filters.add(new TermFilter());
		filters.add(new TermsFilter());
		filters.add(new RangeFilter());
		filters.add(new PageHelperFilter());
		filters.add(new MatchFilter());
		filters.add(new ExistsFilter());
		filters.add(new MinimumShouldMatchFilter());
	}

	@Override
	public void onApplicationEvent(ApplicationContextEvent event) {
		if(initFlag) {
			ApplicationUtils.setContext(event.getApplicationContext());
			initFlag = false;
		}
	}

	public static List<IndexProperty.Property> getIndexProperty(Class indexClazz, Class annotationClazz){
		IndexProperty esIndex = indexMap.get(indexClazz);
		if(esIndex != null && esIndex.getPropertyMap() != null){
			return (List<IndexProperty.Property>) esIndex.getPropertyMap().get(annotationClazz);
		}
		return null;
	}

	public static String getIndexName(Class indexClazz){
		IndexProperty esIndex = indexMap.get(indexClazz);
		if(esIndex == null || StringUtils.isBlank(esIndex.getIndexName())){
			return null;
		}
		return esIndex.getIndexName();
	}

	public static void putIndexNameMap(Class serviceClazz, String indexName){
		indexNameMap.put(serviceClazz, indexName);
	}

	public static void putserviceGenericityClass(Class serviceClazz, Class serviceGenericityClass){
		serviceGenericityClassMap.put(serviceClazz, serviceGenericityClass);
	}

	public static List<QueryFilter> getQueryFilter(){
		return filters;
	}

	private IndexProperty getEsIndex(Class<?> clazz) throws NoSuchMethodException {
		DataSourceProperty esConfig = EsConfigHelper.getCommonEsConfig();

		IndexProperty esIndex = new IndexProperty();
		MultiValueMap propertyMap = new MultiValueMap();
		//索引名称
		Index annotation = clazz.getAnnotation(Index.class);
		esIndex.setIndexName(annotation.name() + esConfig.getEnv());

		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
				for (Annotation declaredAnnotation : declaredAnnotations) {
					//判断是否有指定注解
					Class<? extends Annotation> annotationClazz = declaredAnnotation.annotationType();
					if(ArrayUtils.contains(ES_ANNOTATION, annotationClazz)){
						Method method = clazz.getMethod("get" + EsConfigHelper.upperFirstCase(fieldName));
						IndexProperty.Property property = new IndexProperty.Property(fieldName, method, declaredAnnotation);
						propertyMap.put(annotationClazz,  property);
					}
				}
			}
		}
		esIndex.setPropertyMap(propertyMap);
		return esIndex;
	}

}
