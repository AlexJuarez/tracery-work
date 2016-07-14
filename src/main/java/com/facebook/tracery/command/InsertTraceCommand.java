package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.database.Database;
import com.facebook.tracery.database.trace.DiskPhysOpTable;
import com.facebook.tracery.database.trace.FileInfoTable;
import com.facebook.tracery.database.trace.MasterTraceTable;
import com.facebook.tracery.parse.diskio.DiskTraceItem;
import com.facebook.tracery.parse.diskio.DiskTraceParser;
import com.facebook.tracery.parse.diskio.FileInfo;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

@Parameters(commandDescription = "Insert trace data into the tracery database.")
public class InsertTraceCommand extends AbstractCommand {
  @Parameter(names = "--diskio", description = "The disk I/O trace file to read.")
  private List<String> diskTraceFiles = new ArrayList<>();

  @Parameter(names = "--keep", description =
      "Keep the database file and append to it (if it already exists)." + " (Default: false)")
  private Boolean keep = false;

  @Parameter(description = "<tracery database file>", required = true)
  private List<String> dbFiles = new ArrayList<>();

  public InsertTraceCommand(JCommander jcommander) {
    super(jcommander);
  }

  @Override
  public String getName() {
    return "insert";
  }

  @Override
  public void run() throws Exception {
    if (dbFiles.size() != 1) {
      usage();
    }

    File dbFile = new File(dbFiles.get(0));
    if (!keep && dbFile.exists() && !dbFile.delete()) {
      throw new IOException("Unable to delete database file.");
    }

    Database db = new Database(dbFile);
    try {
      db.connect(Database.Access.READ_WRITE);

      MasterTraceTable masterTraceTable = new MasterTraceTable(db);
      masterTraceTable.create();

      for (String traceFile : diskTraceFiles) {
        insertDiskTrace(new File(traceFile), db, masterTraceTable);
      }
    } finally {
      db.disconnect();
    }
  }

  private void insertDiskTrace(File traceFile, Database db, MasterTraceTable masterTraceTable)
      throws IOException, SQLException, ScriptException, NoSuchMethodException {
    DiskPhysOpTable diskPhysOpTable = new DiskPhysOpTable(db);
    diskPhysOpTable.create();

    FileInfoTable fileInfoTable = new FileInfoTable(db);
    fileInfoTable.create();

    DiskTraceParser traceParser = new DiskTraceParser(traceFile);
    traceParser.parse();

    Long beginTime = System.currentTimeMillis() * 1000L; // FIXME
    Long endTime = beginTime; // FIXME
    String description = "Disk I/O profile."; // FIXME
    int traceIndex = masterTraceTable.addTrace(traceFile.toURI().toURL(), beginTime, endTime,
        description);

    insertDiskTraceFileInfos(traceParser, db, traceIndex, fileInfoTable);
    insertDiskTracePhysOps(traceParser, db, traceIndex, diskPhysOpTable);
  }

  private void insertDiskTraceFileInfos(DiskTraceParser traceParser, Database db, int traceIndex,
                                        FileInfoTable fileInfoTable) throws SQLException {
    try (Statement statement = db.createStatement()) {
      for (FileInfo fileInfo : traceParser.getFileInfos()) {
        fileInfoTable.insertBatch(statement, traceIndex, fileInfo.getName(), fileInfo.getSize(),
            fileInfo.getInode());
      }
      statement.executeBatch();
    }
  }

  private void insertDiskTracePhysOps(DiskTraceParser traceParser, Database db, int traceIndex,
                                      DiskPhysOpTable diskPhysOpTable) throws SQLException {
    try (Statement statement = db.createStatement()) {
      for (DiskTraceItem traceItem : traceParser.getTraceItems()) {
        diskPhysOpTable.insertBatch(statement, traceIndex, traceItem);
      }
      statement.executeBatch();
    }
  }

  @Override
  public String toString() {
    return String.format("[%s] db:%s diskTraceFiles:%s", getClass().getSimpleName(),
        dbFiles,
        diskTraceFiles
    );
  }
}

