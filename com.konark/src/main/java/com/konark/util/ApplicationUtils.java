package com.konark.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationUtils {


    public static ResponseEntity<ResponseModel> buildOkResponse(ResponseModel responseModel) {

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    public static ResponseEntity<ResponseModel> buildErrorResponse(HttpStatus status, ResponseModel responseModel) {

        return ResponseEntity.status(status).body(responseModel);
    }

    public static final String getString(Object object) {

        if (object == null) {
            return "";
        }
        if (object instanceof Date) {
            return formatDate((Date) object);
        }
        return object.toString();
    }


    public static String formatDate(Date date) {

        // TODO: Use ISO date format
        String dateString = null;
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateString = dateFormat.format(date);
        }
        return dateString;
    }
}
