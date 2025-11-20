package com.konark.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public final class StoreException extends RuntimeException {

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		public StoreException() {
	        super();
	    }

	    public StoreException(final String message, final Throwable cause) {
	        super(message, cause);
	    }

	    public StoreException(final String message) {
	        super(message);
	    }

	    public StoreException(final Throwable cause) {
	        super(cause);
	    }
}
