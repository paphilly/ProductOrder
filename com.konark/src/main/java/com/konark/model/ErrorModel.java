package com.konark.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ErrorModel {
	// – a brief, human-readable message about the error
		private String error;

		// the HTTP response code (optional)
		private HttpStatus status;

		// a human-readable explanation of the error
		private String detail;

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

		public HttpStatus getStatus() {
			return status;
		}

		public void setStatus(HttpStatus status) {
			this.status = status;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public ErrorModel(HttpStatus status, String error, String detail) {
			this.status = status;
			this.error = error;
			this.detail = detail;
		}


}
