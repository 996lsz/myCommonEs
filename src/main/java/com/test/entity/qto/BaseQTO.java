package com.test.entity.qto;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.core.annotation.Range;
import com.core.annotation.Term;
import com.core.constant.EsBaseAnnotationConstant;
import com.test.entity.DateValueDeserializer;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * description
 * 
 * @author LSZ 2020/07/25 10:36
 * @contact 648748030@qq.com
 */
@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
public class BaseQTO {

	@Term(fieldName = "is_deleted", bool = EsBaseAnnotationConstant.BoolTypeEnum.MUST_NOT)
	protected Integer isDeleted;

	@Range(fieldName = "ctime", range = EsBaseAnnotationConstant.Range.GTE)
	@JSONField(format="yyyy-MM-dd HH:mm:ss", deserializeUsing = DateValueDeserializer.class)
	protected Date ctimeGte;

	@Range(fieldName = "ctime", range = EsBaseAnnotationConstant.Range.LTE)
	@JSONField(format="yyyy-MM-dd HH:mm:ss", deserializeUsing = DateValueDeserializer.class)
	protected Date ctimeLte;

	@Range(fieldName = "mtime", range = EsBaseAnnotationConstant.Range.GTE)
	@JSONField(format="yyyy-MM-dd HH:mm:ss", deserializeUsing = DateValueDeserializer.class)
	protected Date mtimeGte;

	@Range(fieldName = "mtime", range = EsBaseAnnotationConstant.Range.LTE)
	@JSONField(format="yyyy-MM-dd HH:mm:ss", deserializeUsing = DateValueDeserializer.class)
	protected Date mtimeLte;

	public BaseQTO(){
		this.isDeleted = 1;
	}
    
}
