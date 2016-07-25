package com.facebook.tracery.database;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.facebook.tracery.thrift.table.Category;
import com.facebook.tracery.thrift.table.RawType;
import com.facebook.tracery.thrift.table.Structure;
import com.facebook.tracery.thrift.table.TableColumnType;
import com.facebook.tracery.thrift.table.Unit;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;

public class Column {
  /*
   * Convenience definitions of common column types.
   */
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

  private DbTable dbTable;
  private DbColumn dbColumn;

  public Column(DbTable dbTable, String name, TableColumnType type) {
    this.dbTable = dbTable;

    String typeName = Column.encodeType(type);
    dbColumn = dbTable.addColumn(name, typeName, null);
  }

  public void addForeignKeyConstraint(String constraintName, String refTableName, String
      refColumnName) {
    dbTable.foreignKey(constraintName,
        new String[] {getName()},
        refTableName,
        new String[] {refColumnName});
  }

  public void addPrimaryKeyConstraint(String constraintName) {
    dbTable.primaryKey(constraintName, getName());
  }

  public String getName() {
    return dbColumn.getName();
  }

  public TableColumnType getType() {
    String typeName = dbColumn.getTypeNameSQL();
    return decodeType(typeName);
  }

  public DbColumn getDbColumn() {
    return dbColumn;
  }

  /*
   * See https://www.sqlite.org/datatype3.html#section_3_1 for SQLite rules governing mapping of
   * column names to type affinities.
   */

  // Raw type encodings match SQLite type affinities.
  /* package */ static final BiMap<RawType, String> rawTypeEncodingMap = HashBiMap.create();

  static {
    rawTypeEncodingMap.put(RawType.BOOL, "BOOL"); // numeric
    rawTypeEncodingMap.put(RawType.INT, "INT");
    rawTypeEncodingMap.put(RawType.FLOAT, "FLOAT");
    rawTypeEncodingMap.put(RawType.STRING, "TEXT");
    rawTypeEncodingMap.put(RawType.BINARY, "BLOB");
  }

  // All type info encodings (other than raw type) must not match SQLite type matching rules.
  /* package */ static final BiMap<Unit, String> unitEncodingMap = HashBiMap.create();

  static {
    unitEncodingMap.put(Unit.NONE, "NONE");
    unitEncodingMap.put(Unit.PERCENT, "PCNT");
    unitEncodingMap.put(Unit.BYTES, "BYTES");
    unitEncodingMap.put(Unit.SECONDS, "SEC");
    unitEncodingMap.put(Unit.MILLISECONDS, "MS");
    unitEncodingMap.put(Unit.MICROSECONDS, "US");
    unitEncodingMap.put(Unit.NANOSECONDS, "NS");
  }

  /* package */ static final BiMap<Category, String> categoryEncodingMap = HashBiMap.create();

  static {
    categoryEncodingMap.put(Category.OTHER, "MISC");
    categoryEncodingMap.put(Category.QUANTITY, "QNT");
    categoryEncodingMap.put(Category.ID, "ID");
    categoryEncodingMap.put(Category.DURATION, "DUR");
    categoryEncodingMap.put(Category.TIMESTAMP, "TIME");
    categoryEncodingMap.put(Category.PATH, "PATH");
    categoryEncodingMap.put(Category.URL, "URL");
  }

  /* package */ static final BiMap<Structure, String> structureEncodingMap = HashBiMap.create();

  static {
    structureEncodingMap.put(Structure.SCALAR, "VAL");
    structureEncodingMap.put(Structure.ARRAY, "ARR");
  }

  private static final String SEPARATOR = "_";

  public static String encodeType(TableColumnType type) {
    StringBuilder decoration = new StringBuilder();
    decoration.append(rawTypeEncodingMap.get(type.getRawType()));
    decoration.append(SEPARATOR);
    decoration.append(categoryEncodingMap.get(type.getCategory()));
    decoration.append(SEPARATOR);
    decoration.append(unitEncodingMap.get(type.getUnit()));
    decoration.append(SEPARATOR);
    decoration.append(structureEncodingMap.get(type.getStructure()));
    return decoration.toString();
  }

  public static TableColumnType decodeType(String typeString) {
    String[] typeElements = typeString.split(SEPARATOR);
    if (typeElements.length == 1) {
      // Mathematical operations (except concatenation operator ||) apply NUMERIC affinity
      // to all operands prior to their evaluation.
      // Casting into NUMERIC first does a forced conversion into REAL but then further converts
      // the result into INTEGER if and only if the conversion from REAL to INTEGER is lossless
      // and reversible.
      if (typeString.equalsIgnoreCase("INTEGER")) {
        return INTEGER_COLUMN_TYPE;
      } else if (typeString.equalsIgnoreCase("REAL")) {
        return FLOAT_COLUMN_TYPE;
      } else {
        throw new IllegalArgumentException("Unexpected column type: '" + typeString + "'");
      }
    } else if (typeElements.length == 4) {
      RawType rawType = rawTypeEncodingMap.inverse().get(typeElements[0]);
      if (rawType == null) {
        throw new IllegalArgumentException("Unknown raw type encoding: '" + typeString + "'");
      }
      Category category = categoryEncodingMap.inverse().get(typeElements[1]);
      if (category == null) {
        throw new IllegalArgumentException("Unknown category encoding: '" + typeString + "'");
      }
      Unit unit = unitEncodingMap.inverse().get(typeElements[2]);
      if (unit == null) {
        throw new IllegalArgumentException("Unknown unit encoding: '" + typeString + "'");
      }
      Structure structure = structureEncodingMap.inverse().get(typeElements[3]);
      if (structure == null) {
        throw new IllegalArgumentException("Unknown structure encoding: '" + typeString + "'");
      }
      return new TableColumnType(rawType, category, unit, structure);
    } else {
      throw new IllegalArgumentException("Invalid number of type elements: '" + typeString + "'");
    }
  }
}
