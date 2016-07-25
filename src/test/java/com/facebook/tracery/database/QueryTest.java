package com.facebook.tracery.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.facebook.tracery.thrift.query.Aggregation;
import com.facebook.tracery.thrift.query.BinaryOperation;
import com.facebook.tracery.thrift.query.Grouping;
import com.facebook.tracery.thrift.query.Ordering;
import com.facebook.tracery.thrift.query.Query;
import com.facebook.tracery.thrift.query.QueryResult;
import com.facebook.tracery.thrift.query.ResultColumn;
import com.facebook.tracery.thrift.table.TableColumnType;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class QueryTest {
  private static final String NAME_COLUMN_NAME = "name";
  private static final String AGE_COLUMN_NAME = "age";
  private static final TableColumnType NAME_COLUMN_TYPE = Column.NAME_COLUMN_TYPE;
  private static final TableColumnType AGE_COLUMN_TYPE = Column.COUNT_COLUMN_TYPE;
  private static final String[] NAME_DATA = {"Fido", "Benji", "Oreo", "Bear", "Fido"};
  private static final Integer[] AGE_DATA = {3, 1, 8, 8, 2};

  private Database db;
  private TestTable testTable;
  private Query query;

  @Before
  public void setup() throws ClassNotFoundException, SQLException {
    db = new Database(); // in-memory
    db.connect(Database.Access.READ_WRITE);

    testTable = new TestTable(db);
    testTable.dropIfExists();
    testTable.create();

    try (Statement statement = db.createStatement()) {
      for (int ii = 0; ii < NAME_DATA.length; ii++) {
        String insertQuery =
            new InsertQuery(testTable.getDbTable())
                .addColumn(testTable.getNameColumn().getDbColumn(), NAME_DATA[ii])
                .addColumn(testTable.getAgeColumn().getDbColumn(), AGE_DATA[ii])
                .validate().toString();
        statement.addBatch(insertQuery);
      }
      statement.executeBatch();
    }

    query = new Query();
    query.setSourceTables(Arrays.asList(testTable.getName()));

    // Explicitly set result ordering by index column for result stability.
    Ordering orderByRowId = new Ordering("ROWID", true);
    query.setOrderBy(Arrays.asList(orderByRowId));
  }

  @Test
  public void testQuery() throws SQLException {
    QueryResult queryResult = db.doSelectQuery(query);
    assertNotNull(queryResult);

    List<List<String>> expectedValues = Arrays.asList(
        Arrays.asList("Fido", "3"),
        Arrays.asList("Benji", "1"),
        Arrays.asList("Oreo", "8"),
        Arrays.asList("Bear", "8"),
        Arrays.asList("Fido", "2")
    );
    List<List<String>> actualValues = queryResult.getRows();
    assertEquals(expectedValues, actualValues);

    List<TableColumnType> expectedTypes = Arrays.asList(
        NAME_COLUMN_TYPE,
        AGE_COLUMN_TYPE
    );
    List<TableColumnType> actualTypes = queryResult.getColumnTypes();
    assertEquals(expectedTypes, actualTypes);
  }

  @Test
  public void testQueryResultColumn() throws SQLException {
    ResultColumn resultColumn = new ResultColumn(
        ExpressionFactory.createValueExpression(AGE_COLUMN_NAME),
        Aggregation.NONE);
    query.setResultSet(Arrays.asList(resultColumn));

    QueryResult queryResult = db.doSelectQuery(query);
    assertNotNull(queryResult);

    List<List<String>> expectedValues = Arrays.asList(
        Arrays.asList("3"),
        Arrays.asList("1"),
        Arrays.asList("8"),
        Arrays.asList("8"),
        Arrays.asList("2")
    );
    List<List<String>> actualValues = queryResult.getRows();
    assertEquals(expectedValues, actualValues);

    List<TableColumnType> expectedTypes = Arrays.asList(
        AGE_COLUMN_TYPE
    );
    List<TableColumnType> actualTypes = queryResult.getColumnTypes();
    assertEquals(expectedTypes, actualTypes);
  }

  @Test
  public void testQueryWhere() throws SQLException {
    query.setWhere(ExpressionFactory.createBinaryValueExpression(NAME_COLUMN_NAME, BinaryOperation
        .EQ, "'Fido'"));

    QueryResult queryResult = db.doSelectQuery(query);
    assertNotNull(queryResult);

    List<List<String>> expectedValues = Arrays.asList(
        Arrays.asList("Fido", "3"),
        Arrays.asList("Fido", "2")
    );
    List<List<String>> actualValues = queryResult.getRows();
    assertEquals(expectedValues, actualValues);

    List<TableColumnType> expectedTypes = Arrays.asList(
        NAME_COLUMN_TYPE,
        AGE_COLUMN_TYPE
    );
    List<TableColumnType> actualTypes = queryResult.getColumnTypes();
    assertEquals(expectedTypes, actualTypes);
  }

  @Test
  public void testQueryOrderBy() throws SQLException {
    Ordering orderByAge = new Ordering(AGE_COLUMN_NAME, true);
    Ordering orderByName = new Ordering(NAME_COLUMN_NAME, true);
    Ordering orderByRowId = new Ordering("ROWID", true);
    query.setOrderBy(Arrays.asList(orderByAge, orderByName, orderByRowId));

    QueryResult queryResult = db.doSelectQuery(query);
    assertNotNull(queryResult);

    List<List<String>> expectedValues = Arrays.asList(
        Arrays.asList("Benji", "1"),
        Arrays.asList("Fido", "2"),
        Arrays.asList("Fido", "3"),
        Arrays.asList("Bear", "8"),
        Arrays.asList("Oreo", "8")
    );
    List<List<String>> actualValues = queryResult.getRows();
    assertEquals(expectedValues, actualValues);

    List<TableColumnType> expectedTypes = Arrays.asList(
        NAME_COLUMN_TYPE,
        AGE_COLUMN_TYPE
    );
    List<TableColumnType> actualTypes = queryResult.getColumnTypes();
    assertEquals(expectedTypes, actualTypes);
  }

  @Test
  public void testQueryGroupBy() throws SQLException {
    ResultColumn nameResultColumn = new ResultColumn(
        ExpressionFactory.createValueExpression(NAME_COLUMN_NAME),
        Aggregation.NONE);
    ResultColumn ageSumColumn = new ResultColumn(
        ExpressionFactory.createValueExpression(AGE_COLUMN_NAME),
        Aggregation.SUM);
    query.setResultSet(Arrays.asList(nameResultColumn, ageSumColumn));

    Ordering orderByAgeSum = new Ordering("sum(" + AGE_COLUMN_NAME + ")", true);
    Ordering orderByRowId = new Ordering("ROWID", true);
    query.setOrderBy(Arrays.asList(orderByAgeSum, orderByRowId));

    Grouping groupByName = new Grouping(NAME_COLUMN_NAME);
    query.setGroupBy(Arrays.asList(groupByName));

    QueryResult queryResult = db.doSelectQuery(query);
    assertNotNull(queryResult);

    List<List<String>> expectedValues = Arrays.asList(
        Arrays.asList("Benji", "1"),
        Arrays.asList("Fido", "5"),
        Arrays.asList("Oreo", "8"),
        Arrays.asList("Bear", "8")
    );
    List<List<String>> actualValues = queryResult.getRows();
    assertEquals(expectedValues, actualValues);

    // NOTE: Mathematical operations like sum() cast operands to NUMERIC (INT / FLOAT) affinity.
    List<TableColumnType> expectedTypes = Arrays.asList(
        NAME_COLUMN_TYPE,
        Column.INTEGER_COLUMN_TYPE
    );
    List<TableColumnType> actualTypes = queryResult.getColumnTypes();
    assertEquals(expectedTypes, actualTypes);
  }

  @Test
  public void testQueryLimit() throws SQLException {
    query.setLimit(2);

    QueryResult queryResult = db.doSelectQuery(query);
    assertNotNull(queryResult);

    List<List<String>> expectedValues = Arrays.asList(
        Arrays.asList("Fido", "3"),
        Arrays.asList("Benji", "1")
    );
    List<List<String>> actualValues = queryResult.getRows();
    assertEquals(expectedValues, actualValues);

    List<TableColumnType> expectedTypes = Arrays.asList(
        NAME_COLUMN_TYPE,
        AGE_COLUMN_TYPE
    );
    List<TableColumnType> actualTypes = queryResult.getColumnTypes();
    assertEquals(expectedTypes, actualTypes);
  }

  @Test
  public void testQueryOffset() throws SQLException {
    query.setOffset(2);
    query.setLimit(2);

    QueryResult queryResult = db.doSelectQuery(query);
    assertNotNull(queryResult);

    List<List<String>> expectedValues = Arrays.asList(
        Arrays.asList("Oreo", "8"),
        Arrays.asList("Bear", "8")
    );
    List<List<String>> actualValues = queryResult.getRows();
    assertEquals(expectedValues, actualValues);

    List<TableColumnType> expectedTypes = Arrays.asList(
        NAME_COLUMN_TYPE,
        AGE_COLUMN_TYPE
    );
    List<TableColumnType> actualTypes = queryResult.getColumnTypes();
    assertEquals(expectedTypes, actualTypes);
  }

  public static class TestTable extends Table {
    private Column nameColumn;
    private Column ageColumn;

    protected TestTable(Database db) throws SQLException {
      super(db);
    }

    @Override
    protected void setupColumns() throws SQLException {
      nameColumn = addColumn(NAME_COLUMN_NAME, NAME_COLUMN_TYPE);
      ageColumn = addColumn(AGE_COLUMN_NAME, AGE_COLUMN_TYPE);
    }

    public Column getNameColumn() {
      return nameColumn;
    }

    public Column getAgeColumn() {
      return ageColumn;
    }
  }
}
