package com.core.exception;
/**
 * es status line [HTTP/1.1 429 Too Many Requests] ResponseException
 * 
 * @author LSZ 2020/12/11 15:45
 * @contact 648748030@qq.com
 */
public class EsRuntimeException extends RuntimeException{

	public EsRuntimeException(){
		super();
	}

	public EsRuntimeException(Throwable cause) {
		super(cause);
	}

	public EsRuntimeException(String msg) {
		super(msg);
	}
}
