package com.core.entity.common;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.util.List;

/**
 * description
 * 
 * @author LSZ 2020/08/14 16:10
 * @contact 648748030@qq.com
 */
@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class AggregationsData<T> {

	private Long docCountErrorUpperBound;

	private Long sumOtherDocCount;

	private List<Bucket<T>> buckets;

	private OuterHits<T> hits;
}
