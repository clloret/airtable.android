package com.sybit.airtableandroid;

/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */

public final class IOUtils {

  private IOUtils() {

  }


  public static String convertStreamToString(java.io.InputStream is) {

    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}

