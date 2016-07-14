package com.facebook.tracery.database;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Tracery file database connection wrapper.
 */
public class Database {
  private static final int DEFAULT_STATEMENT_TIMEOUT_SEC = 30;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final File file;
  private Connection connection;

  private DbSpec dbSpec;
  private DbSchema dbSchema;

  public enum Access {
    READ_ONLY,
    READ_WRITE
  }

  public Database(File file) throws ClassNotFoundException {
    this.file = file;

    // Load the sqlite-JDBC driver using the current class loader.
    Class.forName("org.sqlite.JDBC");
  }

  public void connect(Access access) throws SQLException {
    if (connection == null) {
      SQLiteConfig config = new SQLiteConfig();
      config.setReadOnly(access != Access.READ_WRITE);
      connection = DriverManager.getConnection("jdbc:sqlite:" + file.toString(),
          config.toProperties());

      dbSpec = new DbSpec();
      dbSchema = dbSpec.addDefaultSchema();
    }
  }

  public boolean isConnected() {
    return connection != null;
  }

  public Connection getConnection() {
    return connection;
  }

  public void disconnect() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException ex) {
        logger.error("Error closing SQL connection.", ex);
      }
      connection = null;
      dbSpec = null;
      dbSchema = null;
    }
  }

  public DbSpec getDbSpec() {
    return dbSpec;
  }

  public DbSchema getDbSchema() {
    return dbSchema;
  }

  public boolean isReadOnly() throws SQLException {
    checkConnection();
    return connection.isReadOnly();
  }

  @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE")
  public Statement createStatement() throws SQLException {
    checkConnection();

    Statement statement = connection.createStatement();
    statement.setQueryTimeout(DEFAULT_STATEMENT_TIMEOUT_SEC);

    return statement;
  }

  private void checkConnection() {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected to database.");
    }
  }
}

