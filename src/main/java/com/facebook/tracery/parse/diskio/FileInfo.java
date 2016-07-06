package com.facebook.tracery.parse.diskio;

import org.python.core.PyObject;

public class FileInfo {
  private final PyObject pyObj;

  public FileInfo(PyObject pyObj) {
    this.pyObj = pyObj;
  }

  public int getInode() {
    return pyObj.__getattr__("inode").asInt();
  }

  public String getName() {
    return pyObj.__getattr__("name").asString();
  }

  public int getSize() {
    return pyObj.__getattr__("size").asInt();
  }

  public int getReadCount() {
    return pyObj.__getattr__("nr_reads").asInt();
  }

  public int getWriteCount() {
    return pyObj.__getattr__("nr_writes").asInt();
  }

  @Override
  public String toString() {
    return String.format(
        "%s - inode:%d size:%d reads:%d writes:%d",
        getName(),
        getInode(),
        getSize(),
        getReadCount(),
        getWriteCount()
    );
  }
}
