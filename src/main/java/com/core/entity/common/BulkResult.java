package com.core.entity.common;

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
public class BulkResult {

	private Long took;

	private Boolean errors;

	private List<Item> items;

	@Data
	public static class Item {

		private InnerItem create;

		private InnerItem update;

		private InnerItem index;

		private InnerItem delete;

		@Data
		public static class InnerItem {

			@JsonProperty("_index")
			private String index;

			@JsonProperty("_type")
			private String type;

			@JsonProperty("_id")
			private String id;

			@JsonProperty("_version")
			private Integer version;

			private String result;

			private Integer status;

			@JsonProperty("_shards")
			private Shards shards;

			@Data
			public static class Shards{

				private Long total;

				private Long successful;

				private Long failed;
			}
		}

	}

    
}
