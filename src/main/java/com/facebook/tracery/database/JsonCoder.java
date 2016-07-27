package com.facebook.tracery.database;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonCoder {
  private static class SingletonHolder {
    public static final JsonCoder instance = new JsonCoder();
  }

  private Gson gson = new Gson();

  public static JsonCoder getInstance() {
    return SingletonHolder.instance;
  }

  public <T extends Object> String encodeList(List<T> objects) {
    return gson.toJson(objects);
  }

  public <T> List<T> decodeList(String json) {
    Type collectionType = new TypeToken<ArrayList<T>>() {}.getType();
    return gson.fromJson(json, collectionType);
  }
}
