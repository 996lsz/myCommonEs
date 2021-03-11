package com.core.utils;

import com.core.config.EsBaseConfig;
import com.core.entity.common.IndexProperty;
import com.core.entity.config.DataSourceProperty;
import com.core.filter.QueryFilter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * description
 * 
 * @author LSZ 2020/07/23 14:14
 * @contact 648748030@qq.com
 */
public class EsConfigHelper {

	/**
	 * es相关配置
	 */
	@Getter
	@Setter
	private static DataSourceProperty commonEsConfig;

	/**
	 * PO类扫描路径
	 */
	@Getter
	@Setter
	private static String[] scanPackage;

	public static List<IndexProperty.Property> getIndexProperty(Class indexClazz, Class annotationClazz){
		IndexProperty IndexProperty = EsBaseConfig.getIndexMap().get(indexClazz);
		if(IndexProperty != null && IndexProperty.getPropertyMap() != null){
			return (List<com.core.entity.common.IndexProperty.Property>) IndexProperty.getPropertyMap().get(annotationClazz);
		}
		return null;
	}

	public static String getIndexName(Class indexClazz){
		IndexProperty IndexProperty = EsBaseConfig.getIndexMap().get(indexClazz);
		if(IndexProperty == null || StringUtils.isBlank(IndexProperty.getIndexName())){
			throw new RuntimeException("missing index name");
		}
		return IndexProperty.getIndexName();
	}

	public static IndexProperty getIndex(Class indexClazz){
		return EsBaseConfig.getIndexMap().get(indexClazz);
	}

	public static void putIndexNameMap(Class serviceClazz, String indexName){
		EsBaseConfig.getIndexNameMap().put(serviceClazz, indexName);
	}

	public static String getIndexNameMap(Class serviceClazz){
		return EsBaseConfig.getIndexNameMap().get(serviceClazz);
	}

	public static Class getserviceGenericityClass(Class serviceClazz){
		Class clazz = EsBaseConfig.getServiceGenericityClassMap().get(serviceClazz);
		if(clazz != null){
			return clazz;
		}else{
			Class<?> returnType = ApplicationUtils.getServiceClass(serviceClazz);
			//获取该类型后，第一次对该类型进行初始化
			EsBaseConfig.putserviceGenericityClass(clazz, returnType);
			return returnType;
		}
	}

	public static String getServiceClassIndexName(Class clazz) {
		String indexName = EsBaseConfig.getIndexName(clazz);
		if (indexName != null) {
			return indexName;
		} else {
			Class<?> returnType = ApplicationUtils.getServiceClass(clazz);
			indexName = EsBaseConfig.getIndexName(returnType);
			//获取该类型后，第一次对该类型进行初始化
			EsBaseConfig.putIndexNameMap(clazz, indexName);
			return indexName;
		}
	}


	public static List<QueryFilter> getQueryFilter(){
		return EsBaseConfig.getQueryFilter();
	}

	public static String upperFirstCase(String str) {
		// 效率高的方法
		char[] chars = str.toCharArray();
		if (chars[0] >= 'a' && chars[0] <= 'z') {
			chars[0] = (char)(chars[0] - 32);
		}
		return new String(chars);
	}


}
