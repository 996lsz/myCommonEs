package com.core.exception;
/**
 * es status line [HTTP/1.1 429 Too Many Requests] ResponseException
 * 
 * @author LSZ 2020/11/18 10:45
 * @contact 648748030@qq.com
 */
public class TooManyRequestsException extends RuntimeException{

	public TooManyRequestsException(){
		super();
	}

	public TooManyRequestsException(Throwable cause) {
		super(cause);
	}

}
