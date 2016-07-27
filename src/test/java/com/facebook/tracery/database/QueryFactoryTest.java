package com.facebook.tracery.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.facebook.tracery.thrift.query.Aggregation;
import com.facebook.tracery.thrift.query.BinaryOperation;
import com.facebook.tracery.thrift.query.TrinaryOperation;
import com.facebook.tracery.thrift.query.UnaryOperation;
import com.facebook.tracery.thrift.table.Category;
import com.facebook.tracery.thrift.table.RawType;
import com.facebook.tracery.thrift.table.Structure;
import com.facebook.tracery.thrift.table.TableColumnType;
import com.facebook.tracery.thrift.table.Unit;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class QueryFactoryTest {
  @Test
  public void testAggregationDecoding() {
    Set<Aggregation> expectedKeys = new HashSet<>(Arrays.asList(Aggregation.values()));
    expectedKeys.remove(Aggregation.NONE);
    Set<Aggregation> actualKeys = QueryFactory.aggregationSqlMap.keySet();
    assertEquals(expectedKeys, actualKeys);
  }

  @Test
  public void testUnaryOpDecoding() {
    Set<UnaryOperation> expectedKeys = new HashSet<>(Arrays.asList(UnaryOperation.values()));
    Set<UnaryOperation> actualKeys = QueryFactory.unaryOpSqlMap.keySet();
    assertEquals(expectedKeys, actualKeys);
  }

  @Test
  public void testBinaryOpDecoding() {
    Set<BinaryOperation> expectedKeys = new HashSet<>(Arrays.asList(BinaryOperation.values()));
    Set<BinaryOperation> actualKeys = QueryFactory.binaryOpSqlMap.keySet();
    assertEquals(expectedKeys, actualKeys);
  }

  @Test
  public void testTrinaryOpDecoding() {
    Set<TrinaryOperation> expectedKeys = new HashSet<>(Arrays.asList(TrinaryOperation.values()));
    Set<TrinaryOperation> actualKeys = QueryFactory.trinaryOpSqlMap.keySet();
    assertEquals(expectedKeys, actualKeys);
  }
}
