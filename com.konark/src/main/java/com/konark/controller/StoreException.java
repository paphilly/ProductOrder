package com.konark.controller;

public class StoreException extends RuntimeException {
	
	private String ExceptionMessage;

	public String getExceptionMessage() {
		return ExceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		ExceptionMessage = exceptionMessage;
	}

}
