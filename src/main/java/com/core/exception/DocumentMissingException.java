package com.core.exception;

public class DocumentMissingException extends RuntimeException{

    public DocumentMissingException(){
        super();
    }

    public DocumentMissingException(Throwable cause) {
        super(cause);
    }
}
