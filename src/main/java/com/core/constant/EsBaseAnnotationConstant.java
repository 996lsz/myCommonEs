package com.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * description
 * 
 * @author LSZ 2020/07/23 10:31
 * @contact 648748030@qq.com
 */
public class EsBaseAnnotationConstant {

	@NoArgsConstructor
	@AllArgsConstructor
	public enum Range {
		GTE("gte"),
		LTE("lte"),
		GT("gt"),
		LT("lt");

		@Getter
		private String type;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public enum Sort {
		DESC("desc"),
		ASC("asc");

		@Getter
		private String type;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public enum EsAggsTypeEnum {

		MAX("max"),
		MIN("min"),
		AVG("avg"),
		SUM("sum");

		@Getter
		private String type;

	}

	@NoArgsConstructor
	@AllArgsConstructor
	public enum BoolTypeEnum {

		MUST("must"),
		MUST_NOT("must_not"),
		SHOULD("should");

		@Getter
		private String type;

	}

	@NoArgsConstructor
	@AllArgsConstructor
	public enum EsBulkTypeEnum {

		UPDATE("update"),
		DELETE("delete"),
		CREATE("create"),
		INDEX("index");

		@Getter
		private String type;

	}
}
