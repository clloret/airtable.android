package com.sybit.airtableandroid.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * Created by Carlos Lloret
 */

public final class Entity {

  @Expose(serialize = false)
  private transient String id;

  @Expose
  @SerializedName("Text")
  private String text;

  @Expose
  @SerializedName("Number")
  private double number;

  @Expose
  @SerializedName("Checkbox")
  private boolean checkbox;

  @Expose
  @SerializedName("Date")
  private Date date;

  @Expose
  @SerializedName("NullableInteger")
  private Integer nullableInteger;

  public Entity(String id, String text, double number, boolean checkbox, Date date,
      Integer nullableInteger) {

    this.id = id;
    this.text = text;
    this.number = number;
    this.checkbox = checkbox;
    this.date = date;
    this.nullableInteger = nullableInteger;
  }

  public Entity(String text, double number, boolean checkbox, Date date,
      Integer nullableInteger) {

    this.text = text;
    this.number = number;
    this.checkbox = checkbox;
    this.date = date;
    this.nullableInteger = nullableInteger;
  }

  public Entity() {

  }

  public Entity(String id) {

    this.id = id;
  }

  public String getText() {

    return text;
  }

  public void setText(String text) {

    this.text = text;
  }

  public double getNumber() {

    return number;
  }

  public void setNumber(double number) {

    this.number = number;
  }

  public boolean isCheckbox() {

    return checkbox;
  }

  public void setCheckbox(boolean checkbox) {

    this.checkbox = checkbox;
  }

  public Date getDate() {

    return date;
  }

  public void setDate(Date date) {

    this.date = date;
  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    this.id = id;
  }

  public Integer getNullableInteger() {

    return nullableInteger;
  }

  public void setNullableInteger(Integer nullableInteger) {

    this.nullableInteger = nullableInteger;
  }
}
