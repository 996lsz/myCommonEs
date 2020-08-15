package com.core.entity.common;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

/**
 * description
 * 
 * @author LSZ 2020/08/14 16:05
 * @contact 648748030@qq.com
 */
@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class Bucket<T> {

	private String key;

	private Long docCount;

	private AggregationsData<T> aggregationsData;
    
}
