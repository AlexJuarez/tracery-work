package com.facebook.tracery.parse.diskio;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyTuple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class DiskTraceParser {
  private File traceFile;

  private List<DiskTraceItem> traceItems;
  private List<FileInfo> fileInfos;

  public DiskTraceParser(File traceFile) {
    this.traceFile = traceFile;
  }

  /**
   * Parse the trace file.
   *
   * @throws IOException on failure to parse the trace file.
   */
  public void parse() throws IOException {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("python");

      ClassLoader classLoader = getClass().getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream("trace2list.py");
      engine.eval(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

      Invocable invocable = (Invocable) engine;
      PyTuple result = (PyTuple) invocable.invokeFunction(
          "trace2list",
          traceFile.toString(),
          "",
          "",
          ""
      );

      PyList pyTraceItems = (PyList) result.get(0);
      traceItems = new ArrayList<DiskTraceItem>(pyTraceItems.size());
      for (Object obj : pyTraceItems) {
        traceItems.add(new DiskTraceItem((PyObject) obj));
      }

      PyList pyFileInfos = (PyList) result.get(1);
      fileInfos = new ArrayList<FileInfo>(pyFileInfos.size());
      for (Object obj : pyFileInfos) {
        fileInfos.add(new FileInfo((PyObject) obj));
      }
    } catch (NoSuchMethodException ex) {
      throw new RuntimeException(ex);
    } catch (ScriptException ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<DiskTraceItem> getTraceItems() {
    return Collections.unmodifiableList(traceItems);
  }

  public List<FileInfo> getFileInfos() {
    return Collections.unmodifiableList(fileInfos);
  }
}
