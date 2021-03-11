package com.core.exception;
/**
 * es status line [HTTP/1.1 409 Version Conflict] ResponseException
 * 
 * @author LSZ 2020/11/18 10:59
 * @contact 648748030@qq.com
 */
public class VersionConflictException extends RuntimeException{

	public VersionConflictException(){
		super();
	}

	public VersionConflictException(Throwable cause) {
		super(cause);
	}

}
