package com.facebook.tracery.database.trace;

import com.facebook.tracery.database.AbstractTable;
import com.facebook.tracery.database.Database;

/**
 * Trace data root class for binding code (subclasses of this class) to underlying database tables.
 */
public abstract class AbstractTraceTable extends AbstractTable {
  public AbstractTraceTable(Database db) {
    super(db);

    dbTable = db.getDbSchema().addTable(getTableName(getClass()));
  }

  public String getTraceType() {
    // For now, trace table types are identified to clients by the corresponding class name.
    return getClass().getCanonicalName();
  }

  public static String getTableName(Class<? extends AbstractTraceTable> clazz) {
    String className = clazz.getCanonicalName();
    if (className.contains("_")) {
      throw new IllegalArgumentException("Trace table class names must not contain '_'.");
    }
    return className.replace('.', '_');
  }
}
