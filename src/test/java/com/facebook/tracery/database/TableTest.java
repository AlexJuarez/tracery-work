package com.facebook.tracery.database;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class TableTest<T extends Table> {
  private Database db;
  private T testTable;

  @Before
  public void setup() throws ClassNotFoundException, SQLException {
    db = new Database(); // in-memory
    db.connect(Database.Access.READ_WRITE);

    testTable = createTestTable(db);
  }

  @Test
  public void testSchemaMatchesVersion() throws SQLException {
    String expectedSchema = getExpectedSchema();
    assertTableSchemaMatch(expectedSchema);
  }

  protected abstract T createTestTable(Database db) throws SQLException;

  protected abstract String getExpectedSchema();

  private void assertTableSchemaMatch(String expectedSchema) throws
      SQLException {
    String actualInfo = getRawSqliteTableInfo(testTable);

    assertEquals("Database schema change detected for " + testTable.getClass().getSimpleName()
            + ". Be sure to update this test *and* increment DATABASE_SCHEMA_VERSION in "
            + "Database.java.",
        expectedSchema, actualInfo);
  }

  // http://stackoverflow.com/questions/4654762/how-can-one-see-the-structure-of-a-table-in-sqlite
  private static String getRawSqliteTableInfo(Table table) throws SQLException {
    String sql = String.format("PRAGMA table_info(%s);", table.getName());

    StringBuilder result = new StringBuilder();
    try (Statement statement = table.getDatabase().createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

      int colCount = resultSet.getMetaData().getColumnCount();

      while (resultSet.next()) {
        for (int ii = 1; ii <= colCount; ii++) {
          result.append(resultSet.getString(ii));
          if (ii < colCount) {
            result.append("|");
          }
        }
        result.append("\n");
      }
    }
    return result.toString();
  }
}
