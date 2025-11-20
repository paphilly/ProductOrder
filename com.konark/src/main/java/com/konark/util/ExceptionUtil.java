package com.konark.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.konark.model.ErrorModel;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExceptionUtil extends Exception {

	@Autowired
	private ErrorModel errorModel;

	public ExceptionUtil(HttpStatus status, String error, String detail) {
		errorModel.setStatus(status);
		errorModel.setError(error);
		errorModel.setDetail(detail);
	}

	public ErrorModel getErrorModel() {
		return errorModel;
	}

	public void setErrorModel(ErrorModel errorModel) {
		this.errorModel = errorModel;
	}

}
