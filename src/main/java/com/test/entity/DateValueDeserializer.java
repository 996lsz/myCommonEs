package com.test.entity;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

/**
 * 目前兼容10/13位时间戳或 yyyy-MM-dd HH:mm:ss格式字符串
 * 
 * @author LSZ 2020/06/17 15:13
 * @contact 648748030@qq.com
 */
public class DateValueDeserializer implements ObjectDeserializer {

	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		String value = parser.parse(fieldName).toString();
		if(StringUtils.isNumeric(value)){
			if(13 == value.length()){
				return (T) new Date(Long.parseLong(value));
			}else{
				return (T) new Date(Long.parseLong(value) * 1000);
			}
		}else{
			try {
				return (T) DateUtils.parseDate(value, "yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}
