package com.core.entity.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description
 * 
 * @author LSZ 2021/02/22 11:28
 * @contact 648748030@qq.com
 */
@Data
@Accessors(chain = true)
public class DataSourceProperty {

	private String env;

	private String hostName;

	private String userName;

	private String password;

	private Integer port;

}
