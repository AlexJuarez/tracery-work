package com.facebook.tracery.parse.diskio;

import org.python.core.PyList;
import org.python.core.PyObject;

import java.util.ArrayList;
import java.util.List;

public class DiskTraceItem {
  private final PyObject pyObj;

  public DiskTraceItem(PyObject pyObj) {
    this.pyObj = pyObj;
  }

  public double getTimestamp() {
    return pyObj.__getattr__("timestamp").asDouble();
  }

  public double getDuration() {
    return pyObj.__getattr__("duration").asDouble();
  }

  /**
   * Return the type of access.
   */
  public char getType() {
    String typeString = pyObj.__getattr__("type").asString();
    if (typeString.length() == 1) {
      char type = typeString.charAt(0);
      switch (type) {
        case 'R': // read
        case 'W': // write
        case 'S': // sync
        case 'B': // writeback
          return type;
        default:
          break;
      }
    }
    throw new IllegalArgumentException("Unknown event type: '" + typeString + "'");
  }

  public int getCount() {
    return pyObj.__getattr__("count").asInt();
  }

  public String getThreadName() {
    return pyObj.__getattr__("thread_name").asString();
  }

  public int getCpu() {
    return pyObj.__getattr__("cpu").asInt();
  }

  public String getFileName() {
    return pyObj.__getattr__("file_name").asString();
  }

  public int getPageCount() {
    return pyObj.__getattr__("page_count").asInt();
  }

  /**
   * Return the list of pages.
   */
  public List<Integer> getPages() {
    List<Integer> result = new ArrayList<>();
    PyList pyResult = (PyList) pyObj.__getattr__("pages");
    for (Object obj : pyResult) {
      result.add((Integer)obj);
    }
    return result;
  }

  /**
   * Return the list of sectors.
   */
  public List<Integer> getSectors() {
    List<Integer> result = new ArrayList<>();
    PyList pyResult = (PyList) pyObj.__getattr__("sectors");
    for (Object obj : pyResult) {
      result.add((Integer)obj);
    }
    return result;
  }

  @Override
  public String toString() {
    return String.format(
        " | %f |s +%f | %s | %d | %s | %d | %s | %d | %s | %s | ",
        getTimestamp(),
        getDuration(),
        getType(),
        getCount(),
        getThreadName(),
        getCpu(),
        getFileName(),
        getPageCount(),
        getPages(),
        getSectors()
    );
  }
}
