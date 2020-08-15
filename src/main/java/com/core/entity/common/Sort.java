package com.core.entity.common;

import com.core.constant.EsBaseAnnotationConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description
 * 
 * @author LSZ 2020/08/15 10:37
 * @contact 648748030@qq.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sort {

	private String field;

	private EsBaseAnnotationConstant.Sort order;
}
