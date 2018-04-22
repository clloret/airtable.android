package com.sybit.airtableandroid.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * Created by Carlos Lloret
 */

public final class Entity {

  @Expose(serialize = false)
  private String id;

  @SerializedName("Text")
  private String text;

  @SerializedName("Number")
  private double number;

  @SerializedName("Checkbox")
  private boolean checkbox;

  @SerializedName("Date")
  private Date date;

  public Entity(String id, String text, double number, boolean checkbox, Date date) {

    this.id = id;
    this.text = text;
    this.number = number;
    this.checkbox = checkbox;
    this.date = date;
  }

  public Entity(String text, double number, boolean checkbox, Date date) {

    this.text = text;
    this.number = number;
    this.checkbox = checkbox;
    this.date = date;
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

}
