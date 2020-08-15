package com.core.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.core.annotation.Exists;
import com.core.config.EsBaseConfig;
import com.core.entity.common.IndexProperty;

import java.util.Arrays;
import java.util.List;

/**
 * exists查询过滤器（调用链）
 * 
 * @author LSZ 2020/07/22 17:10
 * @contact 648748030@qq.com
 */
public class ExistsFilter implements QueryFilter {

	@Override
	public void execute(JSONObject query, Object dto) throws Exception{
		List<IndexProperty.Property> indexPropertys = EsBaseConfig.getIndexProperty(dto.getClass(), Exists.class);
		if(indexPropertys != null) {
			for (IndexProperty.Property property : indexPropertys) {
				Object value = property.getGetMethod().invoke(dto);
				if (value != null) {
					if (value instanceof Object[]) {
						value = Arrays.asList(value);
					}
					Exists exists = (Exists) property.getAnnotation();
					String must = exists.bool().getType();
					List list = (List) value;
					JSONArray tempQuery = query.getJSONObject("query").getJSONObject("bool").getJSONArray(must);
					for (Object field : list) {
						tempQuery.add(JSONObject.parseObject(String.format("{\"exists\": {\"field\": \"%s\"}}", field.toString())));
					}
				}
			}
		}
	}
}
