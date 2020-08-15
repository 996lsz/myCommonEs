package com.test.entity.qto;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.core.annotation.*;
import com.core.constant.EsBaseAnnotationConstant;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * description
 * 
 * @author LSZ 2020/08/15 16:09
 * @contact 648748030@qq.com
 */
@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
@Index(name = "books")
public class BooksQTO extends BaseQTO {

	@Term(fieldName = "book_id")
	private String bookId;

	@Terms(fieldName = "book_id")
	private List<String> bookIds;

	@Match(fieldName = "book_name")
	private String bookName;

	@Match(fieldName = "author_name")
	private String authorName;

	@Term
	private String code;

	@Term
	private String type;

	@Term(bool = EsBaseAnnotationConstant.BoolTypeEnum.MUST_NOT)
	private String typeMustNot;

	@Range(fieldName = "publish_time", range = EsBaseAnnotationConstant.Range.LTE)
	private Date publishTimeLte;

	@Range(fieldName = "publish_time", range = EsBaseAnnotationConstant.Range.GTE)
	private Date publishTimeGte;
}
