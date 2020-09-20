package com.core.entity.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.core.constant.EsBaseAnnotationConstant;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;

/**
 * description
 * 
 * @author LSZ 2020/08/14 14:27
 * @contact 648748030@qq.com
 */
public class EsQuery {

	private JSONObject jsonObject;

	public EsQuery term(String fieldName, Object value){
		return term(fieldName, value, EsBaseAnnotationConstant.BoolTypeEnum.MUST);
	}

	public EsQuery term(String fieldName, Object value, EsBaseAnnotationConstant.BoolTypeEnum bool){
		String must = bool.getType();
		JSONObject object = JSONObject.parseObject(String.format("{\"term\":{\"%s\":\"%s\"}}", fieldName, value));
		this.jsonObject.getJSONObject("query").getJSONObject("bool").getJSONArray(must).add(object);
		return this;
	}

	public EsQuery terms(String fieldName, Object value) {
		return terms(fieldName, value, EsBaseAnnotationConstant.BoolTypeEnum.MUST);
	}

	public EsQuery terms(String fieldName, Object value, EsBaseAnnotationConstant.BoolTypeEnum boolTypeEnum) {
		if (value instanceof Object[]) {
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
		JSONObject termObject = new JSONObject();
		JSONObject fieldObject = new JSONObject();
		fieldObject.put(fieldName, value);
		termObject.put("terms", fieldObject);
		this.jsonObject.getJSONObject("query").getJSONObject("bool").getJSONArray(boolTypeEnum.getType()).add(termObject);
		return this;
	}

	public EsQuery range(String fieldName, Object value, EsBaseAnnotationConstant.Range range){
		return range(fieldName, value, range, EsBaseAnnotationConstant.BoolTypeEnum.MUST);
	}
	public EsQuery range(String fieldName, Object value, EsBaseAnnotationConstant.Range range, EsBaseAnnotationConstant.BoolTypeEnum boolTypeEnum) {
		JSONObject rangeQueryObject = JSONObject.parseObject(String.format("{\"range\":{\"%s\":{}}}", fieldName));
		if (value instanceof Date) {
			value = DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss");
		}
		rangeQueryObject.getJSONObject("range").getJSONObject(fieldName).put(range.getType(), value);
		this.jsonObject.getJSONObject("query").getJSONObject("bool").getJSONArray(boolTypeEnum.getType()).add(rangeQueryObject);
		return this;
	}

	public EsQuery aggsTerms(String fieldName){
		return aggsTerms(fieldName, 10);
	}

	public EsQuery aggsTerms(String fieldName, Integer size) {
		JSONObject lastAggsJsonObject = getLastAggs(this.jsonObject);
		JSONObject aggsTermsJsonObject = JSONObject.parseObject(String.format("{\"aggregations_data\":{\"terms\":{\"field\":\"%s\",\"size\":%s}}}", fieldName, size));
		lastAggsJsonObject.put("aggs", aggsTermsJsonObject);
		return this;
	}

	public EsQuery aggsTopHits(Integer size, String sortField, EsBaseAnnotationConstant.Sort sort) {
		return aggsTopHits(size, Arrays.asList(new Sort(sortField, sort)));
	}

	public EsQuery aggsTopHits(Integer size, List<Sort> sorts) {
		JSONObject lastAggsJsonObject = getLastAggs(this.jsonObject);
		JSONObject aggsTermsJsonObject = JSONObject.parseObject(String.format("{\"aggregations_data\":{\"top_hits\":{\"size\":1,\"sort\":[]}}}", size));
		JSONArray sortJsonObject = aggsTermsJsonObject.getJSONObject("aggregations_data").getJSONObject("top_hits").getJSONArray("sort");
		for (Sort sort : sorts) {
			sortJsonObject.add(JSONObject.parseObject(String.format("{\"%s\":{\"order\":\"%s\"}}", sort.getField(), sort.getOrder().getType())));
		}
		lastAggsJsonObject.put("aggs", aggsTermsJsonObject);
		return this;
	}


	public EsQuery(){
		jsonObject = JSONObject.parseObject("{\"query\":{\"bool\":{\"must\":[],\"must_not\":[],\"should\":[]}}}");
	}

	public JSONObject getQueryObject(){
		return this.jsonObject;
	}

	private JSONObject getLastAggs(JSONObject jsonObject){
		if(jsonObject.containsKey("aggs")){
			return getLastAggs(jsonObject.getJSONObject("aggs").getJSONObject("aggregations_data"));
		}else{
			return jsonObject;
		}
	}


}
