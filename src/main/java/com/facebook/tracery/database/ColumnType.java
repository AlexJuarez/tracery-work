package com.facebook.tracery.database;

import com.facebook.tracery.thrift.table.Category;
import com.facebook.tracery.thrift.table.RawType;
import com.facebook.tracery.thrift.table.Structure;
import com.facebook.tracery.thrift.table.TableColumnType;
import com.facebook.tracery.thrift.table.Unit;

/*
 * Convenience definitions of common column types.
 */
public class ColumnType {
  public static final TableColumnType NULL_COLUMN_TYPE = new TableColumnType(
      RawType.NULL,
      Category.OTHER,
      Unit.NONE,
      Structure.SCALAR
  );
  public static final TableColumnType INTEGER_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Category.OTHER,
      Unit.NONE,
      Structure.SCALAR
  );
  public static final TableColumnType FLOAT_COLUMN_TYPE = new TableColumnType(
      RawType.FLOAT,
      Category.OTHER,
      Unit.NONE,
      Structure.SCALAR
  );
  public static final TableColumnType TEXT_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Category.OTHER,
      Unit.NONE,
      Structure.SCALAR
  );
  public static final TableColumnType BINARY_COLUMN_TYPE = new TableColumnType(
      RawType.BINARY,
      Category.OTHER,
      Unit.NONE,
      Structure.SCALAR
  );

  public static final TableColumnType INDEX_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Category.ID,
      Unit.NONE,
      Structure.SCALAR);
  public static final TableColumnType COUNT_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Category.QUANTITY,
      Unit.NONE,
      Structure.SCALAR);
  public static final TableColumnType BYTES_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Category.QUANTITY,
      Unit.BYTES,
      Structure.SCALAR);
  public static final TableColumnType DURATION_COLUMN_TYPE = new TableColumnType(
      RawType.FLOAT,
      Category.QUANTITY,
      Unit.SECONDS,
      Structure.SCALAR);
  public static final TableColumnType TIMESTAMP_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Category.TIMESTAMP,
      Unit.MICROSECONDS,
      Structure.SCALAR);
  public static final TableColumnType NAME_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Category.ID,
      Unit.NONE,
      Structure.SCALAR);
  public static final TableColumnType NAME_ARRAY_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Category.ID,
      Unit.NONE,
      Structure.ARRAY);
  public static final TableColumnType ID_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Category.ID,
      Unit.NONE,
      Structure.SCALAR);
  public static final TableColumnType ID_ARRAY_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Category.ID,
      Unit.NONE,
      Structure.ARRAY);
  public static final TableColumnType PATH_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Category.PATH,
      Unit.NONE,
      Structure.SCALAR);
  public static final TableColumnType URL_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Category.URL,
      Unit.NONE,
      Structure.SCALAR);
}
