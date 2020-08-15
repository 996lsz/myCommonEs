package com.core.entity.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * description
 * 
 * @author LSZ 2020/08/14 16:26
 * @contact 648748030@qq.com
 */
@Data
public class OuterHits<T> {
	@JSONField(name = "total")
	private Total esTotal;

	@JSONField(name = "hits")
	private List<InnerHits<T>> hits;
}
