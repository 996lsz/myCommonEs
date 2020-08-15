package com.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.core.annotation.Match;
import com.core.entity.common.IndexProperty;
import com.core.utils.EsConfigHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.List;

/**
 * term查询过滤器（调用链）
 * 
 * @author LSZ 2020/07/22 17:10
 * @contact 648748030@qq.com
 */
public class MatchFilter implements QueryFilter {

	@Override
	public void execute(JSONObject query, Object dto) throws Exception{
		List<IndexProperty.Property> indexPropertys = EsConfigHelper.getIndexProperty(dto.getClass(), Match.class);
		if(indexPropertys != null) {
			for (IndexProperty.Property property : indexPropertys) {
				Object value = property.getGetMethod().invoke(dto);
				if (value != null) {
					//目前只有Date需要做序列化，如果有更多情况再做相关序列化方案
					if (value instanceof Date) {
						value = DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss");
					}
					Match match = (Match) property.getAnnotation();
					String must = match.bool().getType();
					String fieldName = StringUtils.isBlank(match.fieldName()) ? property.getFieldName() : match.fieldName();
					JSONObject object = JSONObject.parseObject(String.format("{\"match\":{\"%s\":\"%s\"}}", fieldName, value));
					query.getJSONObject("query").getJSONObject("bool").getJSONArray(must).add(object);

				}
			}
		}
	}
}
