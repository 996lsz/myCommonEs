package com.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.core.annotation.MinimumShouldMatch;
import com.core.entity.common.IndexProperty;
import com.core.utils.EsConfigHelper;

import java.util.List;

/**
 * minimum_should_match查询过滤器（调用链）
 * 
 * @author LSZ 2020/07/22 17:10
 * @contact 648748030@qq.com
 */
public class MinimumShouldMatchFilter implements QueryFilter {

	@Override
	public void execute(JSONObject query, Object dto) throws Exception{
		List<IndexProperty.Property> indexPropertys = EsConfigHelper.getIndexProperty(dto.getClass(), MinimumShouldMatch.class);
		if(indexPropertys != null) {
			IndexProperty.Property property = indexPropertys.get(0);
				Object value = property.getGetMethod().invoke(dto);
				if (value != null) {
					query.getJSONObject("query").getJSONObject("bool").put("minimum_should_match", value);
			}
		}
	}
}
