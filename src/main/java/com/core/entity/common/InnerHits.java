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
public class InnerHits<T> {

	@JSONField(name = "_index")
	private String index;

	@JSONField(name = "_type")
	private String type;

	@JSONField(name = "_id")
	private String id;

	@JSONField(name = "_score")
	private Double score;

	@JSONField(name = "_source")
	private T source;

	public InnerHits(String index, String id, T source){
		this.index = index;
		this.id = id;
		this.source = source;
	}

/*	public InnerHits<T> getTest(Class<T> t){
		return null;
	}*/
}
