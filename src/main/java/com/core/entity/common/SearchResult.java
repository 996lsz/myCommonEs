package com.core.entity.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description
 * 
 * @author LSZ 2020/07/08 12:08
 * @contact 648748030@qq.com
 */
@Data
@NoArgsConstructor
public class SearchResult<T> {

	@JSONField(name = "_scroll_id")
	private String scrollId;

	private Long took;

	@JSONField(name = "timed_out")
	private Boolean timedOut;

	private OuterHits<T> hits;

	private Aggregations<T> aggregations;

	@Data
	public static class Aggregations<T>{

		private AggregationsData<T> aggregationsData;

	}

}
