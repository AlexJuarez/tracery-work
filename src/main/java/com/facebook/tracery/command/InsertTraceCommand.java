package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.parse.diskio.DiskTraceItem;
import com.facebook.tracery.parse.diskio.DiskTraceParser;
import com.facebook.tracery.parse.diskio.FileInfo;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

@Parameters(commandDescription = "Insert trace data into the tracery database.")
public class InsertTraceCommand extends AbstractCommand {
  @Parameter(names = "--diskio", description = "The disk I/O trace file to read.")
  private List<String> diskTraceFiles = new ArrayList<>();

  public InsertTraceCommand(JCommander jcommander) {
    super(jcommander);
  }

  @Override
  public String getName() {
    return "insert";
  }

  @Override
  public void run() throws Exception {
    for (String traceFile : diskTraceFiles) {
      insertDiskTrace(new File(traceFile));
    }
  }

  private void insertDiskTrace(File traceFile)
      throws IOException, SQLException, ScriptException, NoSuchMethodException {
    DiskTraceParser trace = new DiskTraceParser(traceFile);
    trace.parse();

    for (DiskTraceItem traceItem : trace.getTraceItems()) {
      System.out.println(traceItem.toString());
    }

    for (FileInfo fileInfo : trace.getFileInfos()) {
      System.out.println(fileInfo.toString());
    }
  }

  @Override
  public String toString() {
    return String.format("[%s] diskTraceFiles:%s", getClass().getSimpleName(),
        diskTraceFiles
    );
  }
}
