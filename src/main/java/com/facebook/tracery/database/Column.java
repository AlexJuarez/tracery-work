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
  private DbTable dbTable;
  private DbColumn dbColumn;

  public Column(DbTable dbTable, String name, String typeName) {
    this.dbTable = dbTable;
    dbColumn = dbTable.addColumn(name, typeName, null);
  }

  public Column(DbTable dbTable, String name, TableColumnType type) {
    this(dbTable, name, Column.encodeType(type));
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
    rawTypeEncodingMap.put(RawType.NULL, "NULL");
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
      switch (typeString.toUpperCase()) {
        case "NULL":
          return ColumnType.NULL_COLUMN_TYPE;
        case "INTEGER":
          return ColumnType.INTEGER_COLUMN_TYPE;
        case "REAL":
          return ColumnType.FLOAT_COLUMN_TYPE;
        case "TEXT":
          return ColumnType.TEXT_COLUMN_TYPE;
        case "BLOB":
          return ColumnType.BINARY_COLUMN_TYPE;
        default:
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
