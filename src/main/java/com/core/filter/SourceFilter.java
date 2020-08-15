package com.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.core.annotation.Source;
import com.core.entity.common.IndexProperty;
import com.core.utils.EsConfigHelper;

import java.util.List;

/**
 * term查询过滤器（调用链）
 * 
 * @author LSZ 2020/07/22 17:10
 * @contact 648748030@qq.com
 */
public class SourceFilter implements QueryFilter {

	private static final String CHARACTER = "#";

	@Override
	public void execute(JSONObject query, Object dto) throws Exception{
		List<IndexProperty.Property> indexPropertys = EsConfigHelper.getIndexProperty(dto.getClass(), Source.class);
		if(indexPropertys != null) {
			for (IndexProperty.Property property : indexPropertys) {
				Object value = property.getGetMethod().invoke(dto);
				if (value != null) {
					query.put("_source", value);
				}
			}
		}
	}
}
