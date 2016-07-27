package com.facebook.tracery.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.facebook.tracery.database.ExpressionFactory;
import com.facebook.tracery.database.Table;
import com.facebook.tracery.database.trace.DiskPhysOpTable;
import com.facebook.tracery.thrift.TraceInfo;
import com.facebook.tracery.thrift.TraceryService;
import com.facebook.tracery.thrift.query.Aggregation;
import com.facebook.tracery.thrift.query.BinaryExpression;
import com.facebook.tracery.thrift.query.BinaryOperation;
import com.facebook.tracery.thrift.query.Expression;
import com.facebook.tracery.thrift.query.Grouping;
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
    {
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
          .setResultSet(Arrays.asList(fileNameResultColumn, pageCountResultColumn))
          .setSourceTables(Arrays.asList(diskPhysOpTableName))
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
          .setOrderBy(Arrays.asList(orderByFileName))
          .setLimit(5);

      QueryResult selectReadsResult = client.query(selectReads);
      int rowNum = 1;
      for (List<String> row : selectReadsResult.getRows()) {
        System.out.printf("READ %-2d: %s%n", rowNum++, row.toString());
      }
      System.out.println();
    }

    // List the read counts per page for each *.dex file
    {
      String diskPhysOpTableName = Table.getTableName(DiskPhysOpTable.class);
      ResultColumn fileNameResultColumn = new ResultColumn(
          Expression.valueExpression(new ValueExpression("file_name")),
          Aggregation.NONE
      );

      ResultColumn binNameResultColumn = new ResultColumn(
          Expression.valueExpression(new ValueExpression("atom")),
          Aggregation.NONE
      );
      binNameResultColumn.setResultAlias("binName");

      ResultColumn binCountResultColumn = new ResultColumn(
          Expression.valueExpression(new ValueExpression("atom")),
          Aggregation.COUNT
      );
      binCountResultColumn.setResultAlias("binCount");

      Grouping groupByFileName = new Grouping(DiskPhysOpTable.FILE_NAME_COLUMN_NAME);
      Grouping groupByFileOp = new Grouping(DiskPhysOpTable.FILE_OP_COLUMN_NAME);
      Grouping groupByPage = new Grouping("atom");

      Ordering orderByFileName = new Ordering(DiskPhysOpTable.FILE_NAME_COLUMN_NAME, true);
      Ordering orderByFileOp = new Ordering(DiskPhysOpTable.FILE_OP_COLUMN_NAME, true);
      Ordering orderByBinName = new Ordering(binNameResultColumn.getResultAlias(), true);

      Expression isWriteOp = Expression.binaryExpression(
          new BinaryExpression(
              Expression.valueExpression(
                  new ValueExpression(DiskPhysOpTable.FILE_OP_COLUMN_NAME)
              ),
              BinaryOperation.EQ,
              Expression.valueExpression(
                  new ValueExpression("'R'")
              )
          ));
      Expression isDexFile = Expression.binaryExpression(
          new BinaryExpression(
              Expression.valueExpression(
                  new ValueExpression(DiskPhysOpTable.FILE_NAME_COLUMN_NAME)
              ),
              BinaryOperation.GLOB,
              Expression.valueExpression(
                  new ValueExpression("'*.dex'")
              )
          ));

      Query histogramQuery = new Query()
          .setResultSet(Arrays.asList(fileNameResultColumn, binNameResultColumn,
              binCountResultColumn))
          .setSourceTables(Arrays.asList(
              diskPhysOpTableName,
              String.format("json_each(%s)", DiskPhysOpTable.PAGES_COLUMN_NAME)))
          .setWhere(Expression.binaryExpression(
              new BinaryExpression(isWriteOp, BinaryOperation.AND, isDexFile)))
          .setGroupBy(Arrays.asList(groupByFileName, groupByFileOp, groupByPage))
          .setOrderBy(Arrays.asList(orderByFileName, orderByFileOp, orderByBinName));

      QueryResult histogramResult = client.query(histogramQuery);

      for (List<String> row : histogramResult.getRows()) {
        for (String cell : row) {
          System.out.printf("%s\t", cell);
        }
        System.out.println();
      }
      System.out.println();
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
