/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtableandroid;

import com.sybit.airtableandroid.vo.Records;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

/**
 * Representation Class of Airtable Base.
 *
 * @since 0.1
 */
public class Base {

  private final Map<String, Table> tableMap = new HashMap<>();

  private final String base;

  private final Airtable parent;


  /**
   * Create Airtable Base with given base ID.
   *
   * @param base base ID could be found at https://airtable.com if you select your current base.
   * @param airtable parent airtable object
   */
  public Base(String base, Airtable airtable) {

    this.base = base;
    this.parent = airtable;
  }

  /**
   * Get Airtable object as parent.
   */
  public Airtable airtable() {

    return parent;
  }

  /**
   * Get Table object of given table.
   *
   * @param name Name of required table.
   * @return Object to access table.
   */
  public Table table(String name) {

    return table(name, Records.class);
  }

  /**
   * Get Table object of given table.
   *
   * @param name Name of required table.
   * @param clazz Class representing row of resultsets
   * @return Object to access table.
   */
  public Table table(String name, Class clazz) {

    if (!tableMap.containsKey(name)) {
      Timber.d("Create new instance for table [" + name + "]");
      Table t = new Table(name, clazz);
      t.setParent(this);
      tableMap.put(name, t);
    }

    return tableMap.get(name);
  }

  /**
   * Get base id of base.
   *
   * @return base id
   */
  public String name() {

    return base;
  }
}
