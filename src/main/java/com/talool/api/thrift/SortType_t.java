/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.talool.api.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum SortType_t implements org.apache.thrift.TEnum {
  Asc(0),
  Desc(1);

  private final int value;

  private SortType_t(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static SortType_t findByValue(int value) { 
    switch (value) {
      case 0:
        return Asc;
      case 1:
        return Desc;
      default:
        return null;
    }
  }
}