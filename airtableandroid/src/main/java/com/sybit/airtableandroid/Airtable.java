/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtableandroid;

import android.content.Context;
import com.androidnetworking.AndroidNetworking;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybit.airtableandroid.converter.ListConverter;
import com.sybit.airtableandroid.converter.MapConverter;
import com.sybit.airtableandroid.exception.AirtableException;
import com.sybit.airtableandroid.vo.Attachment;
import com.sybit.airtableandroid.vo.Thumbnail;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import timber.log.Timber;


/**
 * Representation Class of Airtable.
 * It is the entry class to access Airtable data.
 *
 * The API key could be passed to the app by
 * + defining Java property <code>AIRTABLE_API_KEY</code> (e.g. <code>-DAIRTABLE_API_KEY=foo</code>).
 * + defining OS environment variable <code>AIRTABLE_API_KEY</code> (e.g. <code>export
 * AIRTABLE_API_KEY=foo</code>).
 * + defining property file `credentials.properties` in root classpath containing key/value
 * <code>AIRTABLE_API_KEY=foo</code>.
 * + On the other hand the API-key could also be added by using the method
 * <code>Airtable.configure(String apiKey)</code>.
 *
 * @since 0.1
 */
public class Airtable {

  private static final String AIRTABLE_API_KEY = "AIRTABLE_API_KEY";
  private static final String AIRTABLE_BASE = "AIRTABLE_BASE";
  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  private Configuration config;
  private Context context;

  public Airtable(Context context) {

    this.context = context;
  }

  /**
   * Configure, <code>AIRTABLE_API_KEY</code> passed by Java property, enviroment variable
   * or within credentials.properties.
   *
   * @return An Airtable instance configured with GsonObjectMapper
   * @throws AirtableException Missing API-Key
   */
  @SuppressWarnings("UnusedReturnValue")
  public Airtable configure() throws AirtableException {

    return this.configure(new GsonObjectMapper());
  }

  /**
   * Configure, <code>AIRTABLE_API_KEY</code> passed by Java property, enviroment variable
   * or within credentials.properties.
   *
   * @param objectMapper A custom ObjectMapper implementation
   * @return An Airtable instance configured with supplied ObjectMapper
   * @throws AirtableException Missing API-Key
   */
  @SuppressWarnings("UnusedReturnValue")
  public Airtable configure(ObjectMapper objectMapper) throws AirtableException {

    Timber.i("System-Property: Using Java property '-D" + AIRTABLE_API_KEY + "' to get apikey.");
    String airtableApi = System.getProperty(AIRTABLE_API_KEY);

    if (airtableApi == null) {
      Timber.i(
          "Environment-Variable: Using OS environment '" + AIRTABLE_API_KEY + "' to get apikey.");
      airtableApi = System.getenv(AIRTABLE_API_KEY);
    }

    return this.configure(airtableApi, objectMapper);
  }


  /**
   * Configure Airtable.
   *
   * @param apiKey API-Key of Airtable.
   * @return An Airtable instance configured with GsonObjectMapper
   * @throws AirtableException Missing API-Key
   */
  @SuppressWarnings("WeakerAccess")
  public Airtable configure(String apiKey) throws AirtableException {

    return configure(apiKey, new GsonObjectMapper());
  }

  /**
   * Configure Airtable.
   *
   * @param apiKey API-Key of Airtable.
   * @param objectMapper A custom ObjectMapper implementation
   * @throws AirtableException Missing API-Key
   */
  @SuppressWarnings("WeakerAccess")
  public Airtable configure(String apiKey, ObjectMapper objectMapper) throws AirtableException {

    return configure(new Configuration(apiKey, Configuration.ENDPOINT_URL), objectMapper);
  }

  /**
   * @throws AirtableException Missing API-Key or Endpoint
   */
  @SuppressWarnings("WeakerAccess")
  public Airtable configure(Configuration config) throws AirtableException {

    return configure(config, new GsonObjectMapper());
  }


  /**
   * @param objectMapper A custom ObjectMapper implementation
   * @throws AirtableException Missing API-Key or Endpoint
   */
  @SuppressWarnings("WeakerAccess")
  public Airtable configure(Configuration config, ObjectMapper objectMapper)
      throws AirtableException {

    if (config.getApiKey() == null) {
      throw new AirtableException("Missing Airtable API-Key");
    }
    if (config.getEndpointUrl() == null) {
      throw new AirtableException("Missing endpointUrl");
    }

    this.config = config;

    // Only one time
    // TODO: 16/05/2017 Check if objectMapper is necessary
    //Unirest.setObjectMapper(objectMapper);

    // Add specific Converter for Date
    DateTimeConverter dtConverter = new DateConverter();
    ListConverter lConverter = new ListConverter();
    MapConverter thConverter = new MapConverter();

    lConverter.setListClass(Attachment.class);
    thConverter.setMapClass(Thumbnail.class);
    dtConverter.setPattern(DATE_TIME_FORMAT);

    ConvertUtils.register(dtConverter, Date.class);
    ConvertUtils.register(lConverter, List.class);
    ConvertUtils.register(thConverter, Map.class);

    Builder builder = new OkHttpClient().newBuilder()
        .addNetworkInterceptor(new HeadersInterceptor("Bearer " + apiKey()));

    if (config.getTimeout() != null) {
      Timber.i("Set connection timeout to: " + config.getTimeout() + "ms.");
      builder
          .connectTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
          .readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
          .writeTimeout(config.getTimeout(), TimeUnit.MILLISECONDS);
    }

    OkHttpClient okHttpClient = builder.build();

    AndroidNetworking.initialize(context, okHttpClient);

    return this;
  }

  /**
   * Getting the base by given property <code>AIRTABLE_BASE</code>.
   *
   * @return the base object.
   * @throws AirtableException Missing Airtable_BASE
   */
  public Base base() throws AirtableException {

    Timber.i("Using Java property '-D" + AIRTABLE_BASE + "' to get key.");
    String val = System.getProperty(AIRTABLE_BASE);

    if (val == null) {
      Timber.i(
          "Environment-Variable: Using OS environment '" + AIRTABLE_BASE + "' to get base name.");
      val = System.getenv(AIRTABLE_BASE);
    }

    return base(val);
  }

  /**
   * Builder method to create base of given base id.
   *
   * @param base the base id.
   * @throws AirtableException AIRTABLE_BASE was Null
   */
  public Base base(String base) throws AirtableException {

    if (base == null) {
      throw new AirtableException("base was null");
    }

    return new Base(base, this);
  }

  public Configuration getConfig() {

    return config;
  }

  public void setConfig(Configuration config) {

    this.config = config;
  }

  /**
   *
   * @return
   */
  public String endpointUrl() {

    return this.config.getEndpointUrl();
  }

  /**
   *
   * @return
   */
  public String apiKey() {

    return this.config.getApiKey();
  }

  public void setEndpointUrl(String endpointUrl) {

    this.config.setEndpointUrl(endpointUrl);
  }
}
