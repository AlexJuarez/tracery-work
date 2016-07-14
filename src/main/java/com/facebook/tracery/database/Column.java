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
  public static final TableColumnType INDEX_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Unit.NONE,
      Category.ID,
      Structure.SCALAR);
  public static final TableColumnType COUNT_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Unit.NONE,
      Category.QUANTITY,
      Structure.SCALAR);
  public static final TableColumnType BYTES_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Unit.BYTES,
      Category.QUANTITY,
      Structure.SCALAR);
  public static final TableColumnType TEXT_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Unit.NONE,
      Category.OTHER,
      Structure.SCALAR);

  public static final TableColumnType DURATION_COLUMN_TYPE = new TableColumnType(
      RawType.FLOAT,
      Unit.SECONDS,
      Category.QUANTITY,
      Structure.SCALAR);
  public static final TableColumnType TIMESTAMP_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Unit.MICROSECONDS,
      Category.TIMESTAMP,
      Structure.SCALAR);

  public static final TableColumnType NAME_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Unit.NONE,
      Category.ID,
      Structure.SCALAR);
  public static final TableColumnType NAME_ARRAY_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Unit.NONE,
      Category.ID,
      Structure.ARRAY);
  public static final TableColumnType ID_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Unit.NONE,
      Category.ID,
      Structure.SCALAR);
  public static final TableColumnType ID_ARRAY_COLUMN_TYPE = new TableColumnType(
      RawType.INT,
      Unit.NONE,
      Category.ID,
      Structure.ARRAY);

  public static final TableColumnType PATH_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Unit.NONE,
      Category.PATH,
      Structure.SCALAR);
  public static final TableColumnType URL_COLUMN_TYPE = new TableColumnType(
      RawType.STRING,
      Unit.NONE,
      Category.URL,
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
    unitEncodingMap.put(Unit.NONE, "none");
    unitEncodingMap.put(Unit.PERCENT, "pcnt");
    unitEncodingMap.put(Unit.BYTES, "bytes");
    unitEncodingMap.put(Unit.SECONDS, "sec");
    unitEncodingMap.put(Unit.MILLISECONDS, "ms");
    unitEncodingMap.put(Unit.MICROSECONDS, "us");
    unitEncodingMap.put(Unit.NANOSECONDS, "ns");
  }

  /* package */ static final BiMap<Category, String> categoryEncodingMap = HashBiMap.create();

  static {
    categoryEncodingMap.put(Category.OTHER, "misc");
    categoryEncodingMap.put(Category.QUANTITY, "qnt");
    categoryEncodingMap.put(Category.ID, "id");
    categoryEncodingMap.put(Category.DURATION, "dur");
    categoryEncodingMap.put(Category.TIMESTAMP, "time");
    categoryEncodingMap.put(Category.PATH, "path");
    categoryEncodingMap.put(Category.URL, "url");
  }

  /* package */ static final BiMap<Structure, String> structureEncodingMap = HashBiMap.create();

  static {
    structureEncodingMap.put(Structure.SCALAR, "val");
    structureEncodingMap.put(Structure.ARRAY, "arr");
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
    if (typeElements.length != 4) {
      throw new IllegalArgumentException("Invalid number of type elements: '" + typeString + "'");
    }
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
    return new TableColumnType(rawType, unit, category, structure);
  }
}
