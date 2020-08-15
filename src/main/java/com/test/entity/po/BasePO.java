package com.test.entity.po;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.test.entity.DateValueDeserializer;
import lombok.Data;

import java.util.Date;

/**
 * description
 * 
 * @author LSZ 2020/07/25 10:36
 * @contact 648748030@qq.com
 */
@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class BasePO {

	protected Integer isDeleted;

	@JSONField(format="yyyy-MM-dd HH:mm:ss", deserializeUsing = DateValueDeserializer.class)
	protected Date ctime;

	@JSONField(format="yyyy-MM-dd HH:mm:ss", deserializeUsing = DateValueDeserializer.class)
	protected Date mtime;

    
}
