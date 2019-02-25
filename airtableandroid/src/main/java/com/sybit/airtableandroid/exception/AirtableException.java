/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtableandroid.exception;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.sybit.airtableandroid.vo.Error;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * General Exception of API.
 *
 * @since 0.1
 */
public class AirtableException extends Exception {

  private int statusCode;

  public AirtableException(String msg) {

    super(msg);
  }

  public AirtableException(Throwable e) {

    super(e);
    if (e.getCause() instanceof ConnectTimeoutException) {
      Timber.e("possible forgotten to set correct apiKey or base?");
    }

    if (e instanceof ANError) {
      ANError anError = (ANError) e;

      statusCode = anError.getErrorCode();
    }
  }

  /**
   * Default Exception similar to AirtableError of JavaScript Library.
   */
  public AirtableException(String error, String message, Integer status) {

    super(message + " (" + error + ")" + ((status != null) ? " [Http code " + status + "]" : ""));
  }

  @Override
  public String getMessage() {

    Throwable e = this.getCause();

    if (e instanceof ANError) {
      ANError anError = (ANError) e;

      try {

        switch (statusCode) {
          case 401:
            return getString("AUTHENTICATION_REQUIRED",
                "You should provide valid api key to perform this operation", statusCode);

          case 403:
            return getString("NOT_AUTHORIZED", "You are not authorized to perform this operation",
                statusCode);

          case 404:
            String message = (anError.getMessage() != null) ? anError.getMessage()
                : "Could not find what you are looking for";
            return getString("NOT_FOUND", message, statusCode);

          case 413:
            return getString("REQUEST_TOO_LARGE", "Request body is too large", statusCode);

          case 422:
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(anError.getErrorBody());
            Error error = gson.fromJson(jsonObject.getJSONObject("error").toString(), Error.class);

            return getString(error.getType(), error.getMessage(), anError.getErrorCode());

          case 429:
            return getString("TOO_MANY_REQUESTS",
                "You have made too many requests in a short period of time. Please retry your request later",
                statusCode);

          case 500:
            return getString("SERVER_ERROR",
                "Try again. If the problem persists, contact support.", statusCode);

          case 503:
            return getString("SERVICE_UNAVAILABLE",
                "The service is temporarily unavailable. Please retry shortly.", statusCode);

          default:
            return getString("UNDEFINED_ERROR", anError.getMessage(), statusCode);
        }
      } catch (JSONException ex) {
        ex.printStackTrace();
      }
    }

    return super.getMessage();
  }

  private String getString(String error, String message, Integer status) {

    return message + " (" + error + ")" + ((status != null) ? " [Http code " + status + "]" : "");
  }

  public int getStatusCode() {

    return statusCode;
  }
}
