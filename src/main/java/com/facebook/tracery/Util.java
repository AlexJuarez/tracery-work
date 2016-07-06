package com.facebook.tracery;

import java.math.BigInteger;
import java.util.UUID;

public class Util {
  /**
   * Convert an unsigned long to a string in the specified radix.
   *
   * @param value long value to convert
   * @param radix radix to render the value in
   * @return string representation
   */
  public static String convertUnsignedLongToString(long value, int radix) {
    BigInteger bigValue = new BigInteger(Long.toString(value & ~(1L << 63)));
    if (value < 0) {
      bigValue = bigValue.setBit(64);
    }
    return bigValue.toString(radix);
  }

  /**
   * Generate a unique (random) SQL compliant name.
   * @param prefix prefix to add
   * @return name
   */
  public static String generateRandomSqlName(String prefix) {
    UUID uuid = UUID.randomUUID();
    // SQLite name rules (that don't require quoting):
    // -Must contain only only letters, numbers or underline.
    // -Must begin with an letter or underline.
    // Names are case-insensitive.

    // Base 36: digits 0-9, characters a-z
    return prefix + "_"
        + convertUnsignedLongToString(uuid.getMostSignificantBits(), 36)
        + convertUnsignedLongToString(uuid.getLeastSignificantBits(), 36);
  }
}
