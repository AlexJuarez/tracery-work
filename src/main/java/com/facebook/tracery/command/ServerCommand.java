package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.database.Database;
import com.facebook.tracery.service.TraceryServiceHandler;
import com.linecorp.armeria.common.SerializationFormat;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServerListenerAdapter;
import com.linecorp.armeria.server.http.tomcat.TomcatService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.thrift.ThriftService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

      ServerBuilder sb = new ServerBuilder();
      sb.port(port, SessionProtocol.HTTP);
      sb.serviceAt(
          "/api",
          ThriftService.of(handler, SerializationFormat.THRIFT_JSON)
              .decorate(LoggingService::new)).build();
      sb.serviceUnder(
          "/",
          TomcatService.forFileSystem(
              new File(System.getenv("APP_HOME"), "build").getAbsolutePath()));

      Server server = sb.build();
      final CountDownLatch serverStopped = new CountDownLatch(1);
      server.addListener(new ServerListenerAdapter() {
        @Override
        public void serverStopped(Server server) throws Exception {
          serverStopped.countDown();
        }
      });
      server.start().sync();

      logger.info("Tracery service started - serving {} on port {}.", dbFile, port);

      serverStopped.await();
      logger.info("Tracery service stopped.");
    } catch (Database.VersionMisatchException ex) {
      logger.error("The tracery database '{}' has schema version {} but version {} is required. "
              + "Please re-create the database or use a different version of the server.",
          dbFile,
          ex.getActualVersion(),
          ex.getExpectedVersion());
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
