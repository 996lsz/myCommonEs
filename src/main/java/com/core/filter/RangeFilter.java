package com.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.core.annotation.Range;
import com.core.entity.common.IndexProperty;
import com.core.utils.EsConfigHelper;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * term查询过滤器（调用链）
 * 
 * @author LSZ 2020/07/22 17:10
 * @contact 648748030@qq.com
 */
public class RangeFilter implements QueryFilter {

	private static final String CHARACTER = "#";

	@Override
	public void execute(JSONObject query, Object dto) throws Exception{
		MultiValueMap fieldRangeMap = new MultiValueMap();
		List<IndexProperty.Property> indexPropertys = EsConfigHelper.getIndexProperty(dto.getClass(), Range.class);
		if(indexPropertys != null) {
			//根据字段进行range分类
			for (IndexProperty.Property property : indexPropertys) {
				Object value = property.getGetMethod().invoke(dto);
				if (value != null) {
					Range range = (Range) property.getAnnotation();
					String must = range.bool().getType();
					String fieldName = StringUtils.isBlank(range.fieldName()) ? property.getFieldName() : range.fieldName();
					fieldRangeMap.put(must + CHARACTER + fieldName, property);
				}
			}
			//循环FIELD
			Iterator it = fieldRangeMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				String[] splits = key.split(CHARACTER);
				String must = splits[0];
				String fieldName = splits[1];
				indexPropertys = (List<IndexProperty.Property>) fieldRangeMap.get(key);
				//拼接FIELD range
				JSONObject rangeQueryObject = JSONObject.parseObject(String.format("{\"range\":{\"%s\":{}}}", fieldName));
				for (int i = 0; i < indexPropertys.size(); i++) {
					IndexProperty.Property property = indexPropertys.get(i);
					Range range = (Range) property.getAnnotation();
					Object value = property.getGetMethod().invoke(dto);
					//目前只有Date需要做序列化，如果有更多情况再做相关序列化方案
					if (value instanceof Date) {
						value = DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss");
					}
					rangeQueryObject.getJSONObject("range").getJSONObject(fieldName).put(range.range().getType(), value);

				}
				query.getJSONObject("query").getJSONObject("bool").getJSONArray(must).add(rangeQueryObject);
			}
		}
	}
}
