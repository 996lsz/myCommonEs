package com.core.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.core.utils.EsPageHelper;

/**
 * pageHelper查询过滤器,添加分页，排序字段
 * 
 * @author LSZ 2020/07/22 17:10
 * @contact 648748030@qq.com
 */
public class PageHelperFilter implements QueryFilter {

	@Override
	public void execute(JSONObject query, Object dto) throws Exception{
		EsPageHelper.EsPageInfo pageInfo = EsPageHelper.getPageInfo();
		if(pageInfo != null){
			Integer size = pageInfo.getSize();
			Integer from = pageInfo.getFrom();
			JSONArray sort = pageInfo.getSort();
			Boolean trackTotalHits = pageInfo.getTrackTotalHits();
			if(size != null){
				query.put("size", size);
			}
			if(from != null){
				query.put("from", from);
			}
			if(sort != null){
				query.put("sort", sort);
			}
			if(trackTotalHits != null){
				query.put("track_total_hits", trackTotalHits);
			}

		}

	}
}
