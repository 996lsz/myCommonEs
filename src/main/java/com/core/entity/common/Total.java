package com.core.entity.common;

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
public class Total {

	private Long value;

	private String relation;

	public Long getTotalValue(){
		return this.value;
	}
}
