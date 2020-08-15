package com.core.entity.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * description
 * 
 * @author LSZ 2020/08/05 10:43
 * @contact 648748030@qq.com
 */
@Data
public class CUDResult {

	@JSONField(name = "_index")
	private String index;

	@JSONField(name = "_type")
	private String type;

	@JSONField(name = "_id")
	private String id;

	@JSONField(name = "_version")
	private Integer version;

	@JSONField(name = "result")
	private String result;

	@JSONField(name = "_seq_no")
	private Integer seqNo;

	@JSONField(name = "_primary_term")
	private Integer primaryTerm;

	@JSONField(name = "_shards")
	private Shards shards;

	@Data
	public static class Shards {

		private Integer total;

		private Integer successful;

		private Integer failed;

	}


}
