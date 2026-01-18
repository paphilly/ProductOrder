/*******************************************************************************
 * Copyright (c)   2019 , Phani  and/or its affiliates. All rights reserved.
 ******************************************************************************/

package com.konark.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseModel {

   private Map<String, Object> data = new HashMap<String, Object>();

   public Map<String, Object> getData() {
      return data;
   }

   public void setData(Map<String, Object> data) {
      this.data = data;
   }

   public void addModel(Object pageModel) {
      this.data.put(pageModel.getClass().getSimpleName(), pageModel);
   }

   private String message;

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public void addModel(String name, Object pageModel) {
      this.data.put(name, pageModel);
   }
}
