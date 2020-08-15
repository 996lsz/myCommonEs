package com.core.filter;

import com.alibaba.fastjson.JSONObject;

/**
 * description
 *
 * @author LSZ 2020/07/22 17:07
 * @contact 648748030@qq.com
 */
public interface QueryFilter {

	void execute(JSONObject query, Object dto) throws Exception;

}
