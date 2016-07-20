package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.database.Table;
import com.facebook.tracery.database.trace.DiskPhysOpTable;
import com.facebook.tracery.thrift.TraceInfo;
import com.facebook.tracery.thrift.TraceryService;
import com.facebook.tracery.thrift.query.Aggregation;
import com.facebook.tracery.thrift.query.BinaryExpression;
import com.facebook.tracery.thrift.query.BinaryOperation;
import com.facebook.tracery.thrift.query.Expression;
import com.facebook.tracery.thrift.query.Ordering;
import com.facebook.tracery.thrift.query.Query;
import com.facebook.tracery.thrift.query.QueryResult;
import com.facebook.tracery.thrift.query.ResultColumn;
import com.facebook.tracery.thrift.query.ValueExpression;
import com.facebook.tracery.thrift.table.TableColumnInfo;
import com.facebook.tracery.thrift.table.TableInfo;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import java.net.URL;
import java.util.Arrays;
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
        System.out.printf("\tTable '%s' - %d cols x %d rows%n", tableInfo.getName(), tableInfo
            .getColumns().size(), tableInfo.getRowCount());
        int columnNum = 1;
        for (TableColumnInfo columnInfo : tableInfo.getColumns()) {
          System.out.printf("\t\tCOL %-2d: '%s' %s%n", columnNum++, columnInfo.getName(),
              columnInfo.getType());
        }
      }
    }
    System.out.println();

    // List the filenames and count for the first N reads
    String diskPhysOpTableName = Table.getTableName(DiskPhysOpTable.class);
    Ordering orderByFileName = new Ordering(DiskPhysOpTable.FILE_NAME_COLUMN_NAME, true);
    ResultColumn fileNameResultColumn = new ResultColumn(
        Expression.valueExpression(
            new ValueExpression(DiskPhysOpTable.FILE_NAME_COLUMN_NAME)
        ), Aggregation.NONE);
    ResultColumn pageCountResultColumn = new ResultColumn(
        Expression.valueExpression(
            new ValueExpression(DiskPhysOpTable.PAGE_COUNT_COLUMN_NAME)
        ), Aggregation.NONE);

    Query selectReads = new Query()
        // SELECT
        .setResultSet(Arrays.asList(fileNameResultColumn, pageCountResultColumn))
        // FROM
        .setSourceTables(Arrays.asList(diskPhysOpTableName))
        // WHERE
        .setWhere(Expression.binaryExpression(
            new BinaryExpression(
                Expression.valueExpression(
                    new ValueExpression(DiskPhysOpTable.FILE_OP_COLUMN_NAME)
                ),
                BinaryOperation.EQ,
                Expression.valueExpression(
                    new ValueExpression("'R'")
                )
            )))
        // ORDER BY
        .setOrderBy(Arrays.asList(orderByFileName))
        // LIMIT
        .setLimit(5);

    QueryResult selectReadsResult = client.query(selectReads);
    int rowNum = 1;
    for (List<String> row : selectReadsResult.getRows()) {
      System.out.printf("READ %-2d: %s%n", rowNum++, row.toString());
    }
    System.out.println();

    transport.close();
  }

  @Override
  public String toString() {
    return String.format("[%s] port:%d", getClass().getSimpleName(),
        port
    );
  }
}
