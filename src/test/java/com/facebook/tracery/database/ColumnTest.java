package com.facebook.tracery.database;

import com.facebook.tracery.thrift.table.Category;
import com.facebook.tracery.thrift.table.RawType;
import com.facebook.tracery.thrift.table.Structure;
import com.facebook.tracery.thrift.table.TableColumnType;
import com.facebook.tracery.thrift.table.Unit;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ColumnTest {
  @Test
  public void testRawTypeEncoding() {
    assertTrue(Column.rawTypeEncodingMap.size() == RawType.values().length);
  }

  @Test
  public void testUnitEncoding() {
    assertTrue(Column.unitEncodingMap.size() == Unit.values().length);
    for (Unit unit : Unit.values()) {
      assertNotSqlType(Column.unitEncodingMap.get(unit));
    }
  }

  @Test
  public void testCategoryEncoding() {
    assertTrue(Column.categoryEncodingMap.size() == Category.values().length);
    for (Category category : Category.values()) {
      assertNotSqlType(Column.categoryEncodingMap.get(category));
    }
  }

  @Test
  public void testStructureEncoding() {
    assertTrue(Column.structureEncodingMap.size() == Structure.values().length);
    for (Structure structure : Structure.values()) {
      assertNotSqlType(Column.structureEncodingMap.get(structure));
    }
  }

  @Test
  public void testRoundTripEncoding() {
    for (RawType rawType : RawType.values()) {
      for (Category category : Category.values()) {
        for (Unit unit : Unit.values()) {
          for (Structure structure : Structure.values()) {
            TableColumnType origType = new TableColumnType(rawType, category, unit, structure);
            String encoding = Column.encodeType(origType);
            TableColumnType decodedType = Column.decodeType(encoding);
            assertEquals(origType, decodedType);
          }
        }
      }
    }
  }

  /*
   * See https://www.sqlite.org/datatype3.html#section_3_1 for SQLite rules governing mapping of
   * column names to type affinities.
   */
  private void assertNotSqlType(String encoding) {
    assertNotNull(encoding);
    assertFalse(encoding.toUpperCase().contains("INT"));
    assertFalse(encoding.toUpperCase().contains("CHAR"));
    assertFalse(encoding.toUpperCase().contains("CLOB"));
    assertFalse(encoding.toUpperCase().contains("TEXT_COLUMN_TYPE"));
    assertFalse(encoding.toUpperCase().contains("BLOB"));
    assertFalse(encoding.toUpperCase().contains("REAL"));
    assertFalse(encoding.toUpperCase().contains("FLOA"));
    assertFalse(encoding.toUpperCase().contains("DOUB"));
  }
}
