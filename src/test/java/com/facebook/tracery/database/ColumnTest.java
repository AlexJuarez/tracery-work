package com.facebook.tracery.database;

import com.facebook.tracery.thrift.table.Category;
import com.facebook.tracery.thrift.table.RawType;
import com.facebook.tracery.thrift.table.Structure;
import com.facebook.tracery.thrift.table.TableColumnType;
import com.facebook.tracery.thrift.table.Unit;
import org.junit.Assert;
import org.junit.Test;

public class ColumnTest {
  @Test
  public void testRawTypeEncoding() {
    Assert.assertTrue(Column.rawTypeEncodingMap.size() == RawType.values().length);
  }

  @Test
  public void testUnitEncoding() {
    Assert.assertTrue(Column.unitEncodingMap.size() == Unit.values().length);
    for (Unit unit : Unit.values()) {
      assertNotSqlType(Column.unitEncodingMap.get(unit));
    }
  }

  @Test
  public void testCategoryEncoding() {
    Assert.assertTrue(Column.categoryEncodingMap.size() == Category.values().length);
    for (Category category : Category.values()) {
      assertNotSqlType(Column.categoryEncodingMap.get(category));
    }
  }

  @Test
  public void testStructureEncoding() {
    Assert.assertTrue(Column.structureEncodingMap.size() == Structure.values().length);
    for (Structure structure : Structure.values()) {
      assertNotSqlType(Column.structureEncodingMap.get(structure));
    }
  }

  @Test
  public void testRoundTripEncoding() {
    for (RawType rawType : RawType.values()) {
      for (Unit unit : Unit.values()) {
        for (Category category : Category.values()) {
          for (Structure structure : Structure.values()) {
            TableColumnType origType = new TableColumnType(rawType, unit, category, structure);
            String encoding = Column.encodeType(origType);
            TableColumnType decodedType = Column.decodeType(encoding);
            Assert.assertEquals(origType, decodedType);
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
    Assert.assertNotNull(encoding);
    Assert.assertFalse(encoding.toUpperCase().contains("INT"));
    Assert.assertFalse(encoding.toUpperCase().contains("CHAR"));
    Assert.assertFalse(encoding.toUpperCase().contains("CLOB"));
    Assert.assertFalse(encoding.toUpperCase().contains("TEXT_COLUMN_TYPE"));
    Assert.assertFalse(encoding.toUpperCase().contains("BLOB"));
    Assert.assertFalse(encoding.toUpperCase().contains("REAL"));
    Assert.assertFalse(encoding.toUpperCase().contains("FLOA"));
    Assert.assertFalse(encoding.toUpperCase().contains("DOUB"));
  }
}
