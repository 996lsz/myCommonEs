package com.core.config;

import com.core.annotation.*;
import com.core.entity.common.IndexProperty;
import com.core.filter.*;
import com.core.utils.EsConfigHelper;
import lombok.Getter;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

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
public class EsBaseConfig implements ApplicationListener<ContextRefreshedEvent> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Value("${elasticsearch.env}")
	protected String esEnv;

	// 扫描的包名
	final String BASE_PACKAGE = "com.test.entity";

	final String RESOURCE_PATTERN = "/**/*.class";

	private final Class[] ES_ANNOTATION = {Term.class, Range.class, Terms.class, Match.class, Source.class, Id.class, Exists.class, MinimumShouldMatch.class};

	@Getter
	private static Map<Class, IndexProperty> indexMap = new HashMap<>();

	@Getter
	private static Map<Class, String> indexNameMap = new HashMap<>();

	@Getter
	private static Map<Class, Class> serviceGenericityClassMap = new HashMap<>();

	@Getter
	private static List<QueryFilter> filters = new ArrayList<QueryFilter>();

	static {
		filters.add(new TermFilter());
		filters.add(new TermsFilter());
		filters.add(new RangeFilter());
		filters.add(new PageHelperFilter());
		filters.add(new MatchFilter());
		filters.add(new SourceFilter());
		filters.add(new ExistsFilter());
		filters.add(new MinimumShouldMatchFilter());
	}

	public static List<IndexProperty.Property> getIndexProperty(Class indexClazz, Class annotationClazz){
		IndexProperty IndexProperty = indexMap.get(indexClazz);
		if(IndexProperty != null && IndexProperty.getPropertyMap() != null){
			return (List<com.core.entity.common.IndexProperty.Property>) IndexProperty.getPropertyMap().get(annotationClazz);
		}
		return null;
	}

	public static String getIndexName(Class indexClazz){
		IndexProperty IndexProperty = indexMap.get(indexClazz);
		if(IndexProperty == null || StringUtils.isBlank(IndexProperty.getIndexName())){
			return null;
		}
		return IndexProperty.getIndexName();
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

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.info("----通用es扫描------");
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE)
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
						indexMap.put(clazz, getIndexProperty(clazz));
					}
				}
			}
		} catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
			LOGGER.error("读取class失败", e);
		}
	}


	private IndexProperty getIndexProperty(Class<?> clazz) throws NoSuchMethodException {
		IndexProperty IndexProperty = new IndexProperty();
		MultiValueMap propertyMap = new MultiValueMap();
		//索引名称
		Index annotation = clazz.getAnnotation(Index.class);
		IndexProperty.setIndexName(annotation.name() + esEnv);

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
						com.core.entity.common.IndexProperty.Property property = new IndexProperty.Property(fieldName, method, declaredAnnotation);
						propertyMap.put(annotationClazz,  property);
					}
				}
			}
		}
		IndexProperty.setPropertyMap(propertyMap);
		return IndexProperty;
	}

}
