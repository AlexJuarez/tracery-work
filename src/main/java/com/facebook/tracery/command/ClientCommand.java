package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.thrift.FileInfo;
import com.facebook.tracery.thrift.TraceInfo;
import com.facebook.tracery.thrift.TraceryService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

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

    transport = new TSocket("localhost", port);
    transport.open();

    TProtocol protocol = new TBinaryProtocol(transport);
    TraceryService.Client client = new TraceryService.Client(protocol);

    List<TraceInfo> traces = client.getTraces();
    System.out.println(traces);

    List<FileInfo> files = client.getFiles();
    System.out.println(files);

    transport.close();
  }

  @Override
  public String toString() {
    return String.format("[%s] port:%d", getClass().getSimpleName(),
        port
    );
  }
}
