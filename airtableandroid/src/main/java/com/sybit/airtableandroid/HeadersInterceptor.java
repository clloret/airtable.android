package com.sybit.airtableandroid;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class HeadersInterceptor implements Interceptor {

  private final String bearerToken;

  public HeadersInterceptor(String bearerToken) {

    this.bearerToken = bearerToken;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {

    Request originalRequest = chain.request();

    Request newRequest = originalRequest.newBuilder()
        .addHeader("accept", "application/json")
        .addHeader("Content-type", "application/json")
        .addHeader("Authorization", bearerToken)
        .build();
    return chain.proceed(newRequest);
  }
}
