package com.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.core.annotation.Terms;
import com.core.entity.common.IndexProperty;
import com.core.utils.EsConfigHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;

/**
 * terms查询过滤器（调用链）
 * 
 * @author LSZ 2020/07/23 11:13
 * @contact 648748030@qq.com
 */
public class TermsFilter implements QueryFilter {

	@Override
	public void execute(JSONObject query, Object dto) throws Exception{
		List<IndexProperty.Property> indexPropertys = EsConfigHelper.getIndexProperty(dto.getClass(), Terms.class);
		if(indexPropertys != null) {
			for (IndexProperty.Property property : indexPropertys) {
				Object value = property.getGetMethod().invoke(dto);
				if (value != null) {
					//目前只有Date需要做序列化，如果有更多情况再做相关序列化方案
					if (value instanceof Date[]) {
						value = Arrays.asList(value);
					}
					if(value instanceof Collection){
						List list = (List) value;
						if(list.size() > 0 && list.get(0) instanceof Date){
							List<String> replace = new ArrayList<>();
							for (Object temp : list) {
								replace.add(DateFormatUtils.format((Date) temp, "yyyy-MM-dd HH:mm:ss"));
							}
							value = replace;
						}
					}
					Terms terms = (Terms) property.getAnnotation();
					String must = terms.bool().getType();
					String fieldName = StringUtils.isBlank(terms.fieldName()) ? property.getFieldName() : terms.fieldName();
					JSONObject termObject = new JSONObject();
					JSONObject fieldObject = new JSONObject();
					fieldObject.put(fieldName, value);
					termObject.put("terms", fieldObject);
					query.getJSONObject("query").getJSONObject("bool").getJSONArray(must).add(termObject);
				}
			}
		}
	}
}
