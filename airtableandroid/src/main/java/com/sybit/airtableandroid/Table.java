/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtableandroid;

import com.androidnetworking.common.Priority;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.sybit.airtableandroid.exception.AirtableException;
import com.sybit.airtableandroid.vo.Attachment;
import com.sybit.airtableandroid.vo.Delete;
import com.sybit.airtableandroid.vo.PostRecord;
import com.sybit.airtableandroid.vo.RecordItem;
import com.sybit.airtableandroid.vo.Records;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jodd.bean.BeanUtil;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Representation Class of Airtable Tables.
 *
 * @since 0.1
 */
public class Table<T> {

  private static final String DATE_FORMAT = "yyyy-MM-dd";

  private final String name;
  private final Class<T> type;

  private Base parent;

  /**
   *
   * @param name
   * @param type
   */
  public Table(String name, Class<T> type) {

    this.type = type;
    this.name = name;
  }

  /**
   *
   * @param name
   * @param type
   * @param base
   */
  @SuppressWarnings("WeakerAccess")
  public Table(String name, Class<T> type, Base base) {

    this(name, type);
    setParent(base);
  }

  /**
   *
   * @param parent
   */
  public void setParent(Base parent) {

    this.parent = parent;
  }

  /**
   * If no Parameter ser all querys to null.
   */
  public Single<List<T>> select() {

    return select(new Query() {
      @Override
      public Integer getMaxRecords() {

        return null;
      }

      @Override
      public String getView() {

        return null;
      }

      @Override
      public List<Sort> getSort() {

        return null;
      }

      @Override
      public String filterByFormula() {

        return null;
      }

      @Override
      public String[] getFields() {

        return null;
      }

      @Override
      public Integer getPageSize() {

        return null;
      }
    });
  }

  /**
   * Select List of data of table with defined Query Parameters.
   */
  @SuppressWarnings("WeakerAccess")
  public Single<List<T>> select(Query query) {

    Map<String, String> params = new HashMap<>();
    if (query.getFields() != null && query.getFields().length > 0) {
      String[] fields = query.getFields();
      for (String field : fields) {
        params.put("fields[]", field);
      }
    }
    if (query.getMaxRecords() != null) {
      params.put("maxRecords", query.getMaxRecords().toString());
    }
    if (query.getView() != null) {
      params.put("view", query.getView());
    }
    if (query.filterByFormula() != null) {
      params.put("filterByFormula", query.filterByFormula());
    }
    if (query.getPageSize() != null) {
      if (query.getPageSize() > 100) {
        params.put("pageSize", "100");
      } else {
        params.put("pageSize", query.getPageSize().toString());
      }
    }
    if (query.getSort() != null) {
      int i = 0;
      for (Sort sort : query.getSort()) {
        params.put("sort[" + i + "][field]", sort.getField());
        params.put("sort[" + i + "][direction]", sort.getDirection().toString());
      }
    }

    final String BASE_URL = getTableEndpointUrl();

    return Rx2AndroidNetworking.get(BASE_URL)
        .addQueryParameter(params)
        .setPriority(Priority.MEDIUM)
        .build()
        .getObjectObservable(Records.class)
        .onErrorResumeNext(throwable -> {

          return Observable.error(new AirtableException(throwable));
        })
        .map(this::getList)
        .single(new ArrayList<>());
  }

  /**
   * select with Parameter maxRecords
   */
  public Single<List<T>> select(final Integer maxRecords) {

    return select(new Query() {
      @Override
      public Integer getMaxRecords() {

        return maxRecords;
      }

      @Override
      public String getView() {

        return null;
      }

      @Override
      public List<Sort> getSort() {

        return null;
      }

      @Override
      public String filterByFormula() {

        return null;
      }

      @Override
      public String[] getFields() {

        return null;
      }

      @Override
      public Integer getPageSize() {

        return null;
      }
    });
  }

  /**
   * Select data of table by definied view.
   */
  public Single<List<T>> select(final String view) {

    return select(new Query() {
      @Override
      public Integer getMaxRecords() {

        return null;
      }

      @Override
      public String getView() {

        return view;
      }

      @Override
      public List<Sort> getSort() {

        return null;
      }

      @Override
      public String filterByFormula() {

        return null;
      }

      @Override
      public String[] getFields() {

        return null;
      }

      @Override
      public Integer getPageSize() {

        return null;
      }
    });
  }

  /**
   * Select data of table by definied view.
   * And filter by formula
   */
  public Single<List<T>> select(final String view, final String filterByFormula) {

    return select(new Query() {
      @Override
      public Integer getMaxRecords() {

        return null;
      }

      @Override
      public String getView() {

        return view;
      }

      @Override
      public List<Sort> getSort() {

        return null;
      }

      @Override
      public String filterByFormula() {

        return filterByFormula;
      }

      @Override
      public String[] getFields() {

        return null;
      }

      @Override
      public Integer getPageSize() {

        return null;
      }
    });
  }

  /**
   * select Table data with defined sortation
   */
  public Single<List<T>> select(Sort sortation) {

    final List<Sort> sortList = new ArrayList<>();
    sortList.add(sortation);

    return select(new Query() {
      @Override
      public Integer getMaxRecords() {

        return null;
      }

      @Override
      public String getView() {

        return null;
      }

      @Override
      public List<Sort> getSort() {

        return sortList;
      }

      @Override
      public String filterByFormula() {

        return null;
      }

      @Override
      public String[] getFields() {

        return null;
      }

      @Override
      public Integer getPageSize() {

        return null;
      }
    });
  }

  /**
   * select only Table data with defined Fields
   */
  public Single<List<T>> select(final String[] fields) {

    return select(new Query() {
      @Override
      public Integer getMaxRecords() {

        return null;
      }

      @Override
      public String getView() {

        return null;
      }

      @Override
      public List<Sort> getSort() {

        return null;
      }

      @Override
      public String filterByFormula() {

        return null;
      }

      @Override
      public String[] getFields() {

        return fields;
      }

      @Override
      public Integer getPageSize() {

        return null;
      }
    });
  }

  /**
   * Get List of records of response.
   */
  private List<T> getList(Records records) {

    final List<T> list = new ArrayList<>();

    for (Map<String, Object> record : records.getRecords()) {
      T item = null;
      try {
        item = transform(record, this.type.newInstance());
      } catch (InstantiationException | IllegalAccessException e) {
        Timber.e(e, e.getMessage());
      }
      list.add(item);
    }

    return list;
  }

  /**
   * Find record by given id.
   *
   * @param id id of record.
   * @return searched record.
   */
  public Maybe<T> find(String id) {

    final String BASE_URL = getTableEndpointUrl() + "/{id}";

    return Rx2AndroidNetworking.get(BASE_URL)
        .addPathParameter("id", id)
        .setPriority(Priority.MEDIUM)
        .build()
        .getObjectObservable(RecordItem.class)
        .onErrorResumeNext(throwable -> {

          return Observable.error(new AirtableException(throwable));
        })
        .map(record -> transform(record, type.newInstance()))
        .singleElement();
  }

  /**
   * Create Record of given Item
   *
   * @param item the item to be created
   * @return the created item
   */
  public Maybe<T> create(T item) {

    JSONObject jsonObject;
    try {
      checkProperties(item);

      PostRecord body = new PostRecord<>();
      body.setFields(item);

      Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
      jsonObject = new JSONObject(gson.toJson(body));

    } catch (Exception ex) {
      return Maybe.create(subscriber -> subscriber.onError(ex));
    }

    final String BASE_URL = getTableEndpointUrl();

    return Rx2AndroidNetworking.post(BASE_URL)
        .addJSONObjectBody(jsonObject)
        .setContentType("application/json; charset=utf-8")
        .setPriority(Priority.MEDIUM)
        .build()
        .getObjectObservable(RecordItem.class)
        .onErrorResumeNext(throwable -> {

          return Observable.error(new AirtableException(throwable));
        })
        .map(record -> transform(record, type.newInstance()))
        .singleElement();
  }

  public Maybe<T> update(T item) {

    String id;
    JSONObject jsonObject;
    try {
      id = getIdOfItem(item);

      PostRecord body = new PostRecord<T>();
      body.setFields(filterFields(item));

      Gson gson = new GsonBuilder()
          .setDateFormat(DATE_FORMAT)
          .serializeNulls()
          .create();
      jsonObject = new JSONObject(gson.toJson(body));

    } catch (Exception ex) {
      return Maybe.create(subscriber -> subscriber.onError(ex));
    }

    final String BASE_URL = getTableEndpointUrl() + "/{id}";

    return Rx2AndroidNetworking.patch(BASE_URL)
        .addPathParameter("id", id)
        .addJSONObjectBody(jsonObject)
        .setContentType("application/json; charset=utf-8")
        .setPriority(Priority.MEDIUM)
        .build()
        .getObjectObservable(RecordItem.class)
        .onErrorResumeNext(throwable -> {

          return Observable.error(new AirtableException(throwable));
        })
        .map(record -> transform(record, type.newInstance()))
        .singleElement();
  }

  public T replace(T item) {

    throw new UnsupportedOperationException("not yet implemented");
  }

  /**
   * Delete Record by given id
   */

  public Maybe<Boolean> destroy(final String id) {

    final String BASE_URL = getTableEndpointUrl() + "/{id}";

    return Rx2AndroidNetworking.delete(BASE_URL)
        .addPathParameter("id", id)
        .setPriority(Priority.MEDIUM)
        .build()
        .getObjectObservable(Delete.class)
        .onErrorResumeNext(throwable -> {

          return Observable.error(new AirtableException(throwable));
        })
        .map(Delete::isDeleted)
        .singleElement();
  }

  /**
   *
   * @return
   */
  private Base base() {

    return parent;
  }

  /**
   * Get the endpoint for the specified table.
   *
   * @return URL of tables endpoint.
   */
  private String getTableEndpointUrl() {

    return base().airtable().endpointUrl() + "/" + base().name() + "/" + this.name;
  }

  /**
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  private T transform(Map<String, Object> record, T retval) {

    for (String key : record.keySet()) {
      if ("fields".equals(key)) {
        //noinspection unchecked
        retval = transform((Map<String, Object>) record.get("fields"), retval);
      } else {
        setProperty(retval, key, record.get(key));
      }
    }

    return retval;
  }

  /**
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  private T transform(RecordItem record, T retval) {

    setProperty(retval, "id", record.getId());
    setProperty(retval, "createdTime", record.getCreatedTime());

    retval = transform(record.getFields(), retval);

    return retval;
  }

  /**
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private void setProperty(T retval, String key, Object value) {

    String property = key2property(key);

    for (Field f : this.type.getDeclaredFields()) {
      final SerializedName annotation = f.getAnnotation(SerializedName.class);

      if (annotation != null && property.equalsIgnoreCase(annotation.value())) {
        property = f.getName();
        break;
      }
    }

    if (propertyExists(retval, property)) {
      BeanUtil.pojo.setProperty(retval, property, value);
    } else {
      Timber.w(
          retval.getClass() + " does not support public setter for existing property [" + property
              + "]");
    }
  }

  /**
   * Convert AirTable ColumnName to Java PropertyName.
   */
  private String key2property(String key) {

    if (key.contains(" ") || key.contains("-")) {
      Timber.w("Annotate columns having special characters by using @SerializedName for property: ["
          + key + "]");
    }
    String property = key.trim();
    property = property.substring(0, 1).toLowerCase() + property.substring(1, property.length());

    return property;
  }

  /**
   * Check if writable property exists.
   *
   * @param bean bean to inspect
   * @param property name of property
   * @return true if writable property exists.
   */
  private static boolean propertyExists(Object bean, String property) {

    return BeanUtil.pojo.hasProperty(bean, property);
  }

  /**
   * Checks if the Property Values of the item are valid for the Request.
   */
  private void checkProperties(T item)
      throws AirtableException {

    if (propertyExists(item, "id") || propertyExists(item, "createdTime")) {
      Field[] attributes = item.getClass().getDeclaredFields();
      for (Field attribute : attributes) {
        String name = attribute.getName();
        if (name.equals("id") || name.equals("createdTime")) {
          if (BeanUtil.pojo.getProperty(item, attribute.getName()) != null) {
            throw new AirtableException("Property " + name + " should be null!");
          }
        } else if (name.equals("photos")) {

          List<Attachment> obj = BeanUtil.pojo.getProperty(item, "photos");
          checkPropertiesOfAttachement(obj);
        }
      }
    }
  }

  private void checkPropertiesOfAttachement(List<Attachment> attachements)
      throws AirtableException {

    if (attachements != null) {
      for (int i = 0; i < attachements.size(); i++) {
        if (propertyExists(attachements.get(i), "id")
            || propertyExists(attachements.get(i), "size")
            || propertyExists(attachements.get(i), "type")
            || propertyExists(attachements.get(i), "filename")) {
          Field[] attributesPhotos = attachements.getClass().getDeclaredFields();
          for (Field attributePhoto : attributesPhotos) {
            String namePhotoAttribute = attributePhoto.getName();
            if (namePhotoAttribute.equals("id") || namePhotoAttribute.equals("size")
                || namePhotoAttribute.equals("Tpye") || namePhotoAttribute.equals("filename")) {
              if (BeanUtil.pojo.getProperty(attachements.get(i), namePhotoAttribute) != null) {
                throw new AirtableException("Property " + namePhotoAttribute + " should be null!");
              }
            }
          }
        }
      }
    }
  }

  /**
   * Get the String Id from the item.
   */

  private String getIdOfItem(T item)
      throws AirtableException {

    if (propertyExists(item, "id")) {
      String id = BeanUtil.pojo.getProperty(item, "id");
      if (id != null) {
        return id;
      }
    }
    throw new AirtableException("Id of " + item + " not Found!");
  }

  /**
   * Filter the Fields of the PostRecord Object. Id and created Time are set to null so Object
   * Mapper doesent convert them to JSON.
   */
  private T filterFields(T item) {

    System.out.println(item);

    Field[] attributes = item.getClass().getDeclaredFields();

    for (Field attribute : attributes) {
      String name = attribute.getName();
      if ((name.equals("id") || name.equals("createdTime")) && (
          BeanUtil.pojo.getProperty(item, name) != null)) {
        BeanUtil.pojo.setProperty(item, name, null);
      }
    }

    return item;
  }
}
