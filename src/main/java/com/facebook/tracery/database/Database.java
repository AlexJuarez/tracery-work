package com.facebook.tracery.database;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

  private final Logger logger = LogManager.getLogger(getClass());

  private final File file;
  private Connection connection;

  protected DbSpec dbSpec;
  protected DbSchema dbSchema;

  public enum Access {
    READ_ONLY,
    READ_WRITE
  }

  public Database(File file) throws ClassNotFoundException {
    this.file = file;
  }

  public void disconnect() {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException ex) {
        logger.error(ex);
      }
      connection = null;
      dbSpec = null;
      dbSchema = null;
    }
  }

  public void connect(Access access) throws ClassNotFoundException, SQLException {
    // Load the sqlite-JDBC driver using the current class loader.
    Class.forName("org.sqlite.JDBC");

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

