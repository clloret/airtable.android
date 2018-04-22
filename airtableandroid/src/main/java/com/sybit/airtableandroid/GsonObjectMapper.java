/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sybit.airtableandroid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import timber.log.Timber;

/**
 * @author fzr
 */
class GsonObjectMapper extends ObjectMapper {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  private final Gson gson;

  public GsonObjectMapper() {

    gson = new GsonBuilder().setDateFormat(DATE_TIME_FORMAT).create();
  }

  public <T> T readValue(String value, Class<T> valueType) {

    Timber.d("readValue: %s", value);
    return gson.fromJson(value, valueType);
  }

  public String writeValue(Object value) {

    Timber.d("writeValue: %s", value);
    return gson.toJson(value);
  }
}
