package com.test.entity.po;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.core.annotation.Id;
import com.core.annotation.Index;
import lombok.Data;

import java.util.Date;

/**
 * description
 * 
 * @author LSZ 2020/08/15 16:09
 * @contact 648748030@qq.com
 */
@Data
@JSONType(naming = PropertyNamingStrategy.SnakeCase)
@Index(name = "books")
public class BooksPO extends BasePO {

	@Id
	private String bookId;

	private String bookName;

	private String cover;

	private String authorName;

	private String code;

	private String type;

	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private Date publishTime;

}
