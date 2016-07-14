package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.database.Database;
import com.facebook.tracery.service.TraceryServiceHandler;
import com.facebook.tracery.thrift.TraceryService;
import org.apache.logging.log4j.Level;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Serve trace data from the tracery database.")
public class ServerCommand extends AbstractCommand {
  public static final int DEFAULT_PORT = 9090;

  @Parameter(names = "--port", description = "The HTTP port to listen for connections on. "
      + "(Default: " + DEFAULT_PORT + ")")
  private Integer port = DEFAULT_PORT;

  @Parameter(description = "<tracery database file>", required = true)
  private List<String> dbFiles = new ArrayList<>();

  public ServerCommand(JCommander jcommander) {
    super(jcommander);
  }

  @Override
  public String getName() {
    return "server";
  }

  @Override
  public void run() throws Exception {
    if (dbFiles.size() != 1) {
      usage();
    }

    File dbFile = new File(dbFiles.get(0));
    Database db = new Database(dbFile);
    try {
      db.connect(Database.Access.READ_ONLY);

      TraceryServiceHandler handler = new TraceryServiceHandler(db);
      TraceryService.Processor<TraceryServiceHandler> processor =
          new TraceryService.Processor<>(handler);

      TServerTransport serverTransport = new TServerSocket(port);
      TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

      logger.printf(Level.INFO, "Tracery service started - serving %s on port %d.", dbFile, port);

      server.serve();
    } finally {
      db.disconnect();
    }
  }

  @Override
  public String toString() {
    return String.format("[%s] db:%s port:%d", getClass().getSimpleName(),
        dbFiles,
        port
    );
  }
}
