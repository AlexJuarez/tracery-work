package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.thrift.TraceInfo;
import com.facebook.tracery.thrift.TraceryService;
import com.facebook.tracery.thrift.table.TableInfo;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import java.net.URL;
import java.util.List;

@Parameters(commandDescription = "Test client for tracery service.")
public class ClientCommand extends AbstractCommand {

  @Parameter(names = "--port", description = "The HTTP port to connect on." + " (Default: "
      + ServerCommand.DEFAULT_PORT + ")")
  private Integer port = ServerCommand.DEFAULT_PORT;

  public ClientCommand(JCommander jcommander) {
    super(jcommander);
  }

  @Override
  public String getName() {
    return "client";
  }

  @Override
  public void run() throws Exception {
    System.out.println(this);

    TTransport transport;

    // The server (which is written more for JavaScript clients) requires JSON over HTTP
    transport = new THttpClient(new URL("http", "localhost", port, "/api").toString());
    transport.open();

    TProtocol protocol = new TJSONProtocol(transport);
    TraceryService.Client client = new TraceryService.Client(protocol);

    List<TraceInfo> traces = client.getTraces();
    for (TraceInfo trace : traces) {
      System.out.println(trace);
      for (String tableName : trace.getTableNames()) {
        TableInfo tableInfo = client.getTable(tableName);
        System.out.println("\t" + tableName + ": " + tableInfo);
      }
    }

    transport.close();
  }

  @Override
  public String toString() {
    return String.format("[%s] port:%d", getClass().getSimpleName(),
        port
    );
  }
}
