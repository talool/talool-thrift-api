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

public enum SocialNetwork_t implements org.apache.thrift.TEnum {
  FACEBOOK(0),
  TWITTER(1),
  PINTEREST(2);

  private final int value;

  private SocialNetwork_t(int value) {
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
  public static SocialNetwork_t findByValue(int value) { 
    switch (value) {
      case 0:
        return FACEBOOK;
      case 1:
        return TWITTER;
      case 2:
        return PINTEREST;
      default:
        return null;
    }
  }
}
