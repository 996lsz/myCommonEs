package com.core.service;


import com.core.utils.ApplicationUtils;

/**
 * description
 *
 * @author LSZ 2020/08/03 17:12
 * @contact 648748030@qq.com
 */
public interface ProxySelf<T> {

	default T self(){
		return (T) ApplicationUtils.getContext().getBean(this.getClass());
	}

}
