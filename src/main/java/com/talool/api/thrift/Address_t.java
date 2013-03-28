/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.talool.api.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Address_t implements org.apache.thrift.TBase<Address_t, Address_t._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Address_t");

  private static final org.apache.thrift.protocol.TField ADDRESS_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("addressId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField ADDRESS1_FIELD_DESC = new org.apache.thrift.protocol.TField("address1", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField ADDRESS2_FIELD_DESC = new org.apache.thrift.protocol.TField("address2", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField CITY_FIELD_DESC = new org.apache.thrift.protocol.TField("city", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField STATE_PROVINCE_COUNTY_FIELD_DESC = new org.apache.thrift.protocol.TField("stateProvinceCounty", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField ZIP_FIELD_DESC = new org.apache.thrift.protocol.TField("zip", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField COUNTRY_FIELD_DESC = new org.apache.thrift.protocol.TField("country", org.apache.thrift.protocol.TType.STRING, (short)7);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new Address_tStandardSchemeFactory());
    schemes.put(TupleScheme.class, new Address_tTupleSchemeFactory());
  }

  public long addressId; // optional
  public String address1; // required
  public String address2; // optional
  public String city; // optional
  public String stateProvinceCounty; // optional
  public String zip; // optional
  public String country; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ADDRESS_ID((short)1, "addressId"),
    ADDRESS1((short)2, "address1"),
    ADDRESS2((short)3, "address2"),
    CITY((short)4, "city"),
    STATE_PROVINCE_COUNTY((short)5, "stateProvinceCounty"),
    ZIP((short)6, "zip"),
    COUNTRY((short)7, "country");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ADDRESS_ID
          return ADDRESS_ID;
        case 2: // ADDRESS1
          return ADDRESS1;
        case 3: // ADDRESS2
          return ADDRESS2;
        case 4: // CITY
          return CITY;
        case 5: // STATE_PROVINCE_COUNTY
          return STATE_PROVINCE_COUNTY;
        case 6: // ZIP
          return ZIP;
        case 7: // COUNTRY
          return COUNTRY;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ADDRESSID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.ADDRESS_ID,_Fields.ADDRESS2,_Fields.CITY,_Fields.STATE_PROVINCE_COUNTY,_Fields.ZIP,_Fields.COUNTRY};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ADDRESS_ID, new org.apache.thrift.meta_data.FieldMetaData("addressId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.ADDRESS1, new org.apache.thrift.meta_data.FieldMetaData("address1", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ADDRESS2, new org.apache.thrift.meta_data.FieldMetaData("address2", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CITY, new org.apache.thrift.meta_data.FieldMetaData("city", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STATE_PROVINCE_COUNTY, new org.apache.thrift.meta_data.FieldMetaData("stateProvinceCounty", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ZIP, new org.apache.thrift.meta_data.FieldMetaData("zip", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.COUNTRY, new org.apache.thrift.meta_data.FieldMetaData("country", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Address_t.class, metaDataMap);
  }

  public Address_t() {
  }

  public Address_t(
    String address1)
  {
    this();
    this.address1 = address1;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Address_t(Address_t other) {
    __isset_bitfield = other.__isset_bitfield;
    this.addressId = other.addressId;
    if (other.isSetAddress1()) {
      this.address1 = other.address1;
    }
    if (other.isSetAddress2()) {
      this.address2 = other.address2;
    }
    if (other.isSetCity()) {
      this.city = other.city;
    }
    if (other.isSetStateProvinceCounty()) {
      this.stateProvinceCounty = other.stateProvinceCounty;
    }
    if (other.isSetZip()) {
      this.zip = other.zip;
    }
    if (other.isSetCountry()) {
      this.country = other.country;
    }
  }

  public Address_t deepCopy() {
    return new Address_t(this);
  }

  public void clear() {
    setAddressIdIsSet(false);
    this.addressId = 0;
    this.address1 = null;
    this.address2 = null;
    this.city = null;
    this.stateProvinceCounty = null;
    this.zip = null;
    this.country = null;
  }

  public long getAddressId() {
    return this.addressId;
  }

  public Address_t setAddressId(long addressId) {
    this.addressId = addressId;
    setAddressIdIsSet(true);
    return this;
  }

  public void unsetAddressId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ADDRESSID_ISSET_ID);
  }

  /** Returns true if field addressId is set (has been assigned a value) and false otherwise */
  public boolean isSetAddressId() {
    return EncodingUtils.testBit(__isset_bitfield, __ADDRESSID_ISSET_ID);
  }

  public void setAddressIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ADDRESSID_ISSET_ID, value);
  }

  public String getAddress1() {
    return this.address1;
  }

  public Address_t setAddress1(String address1) {
    this.address1 = address1;
    return this;
  }

  public void unsetAddress1() {
    this.address1 = null;
  }

  /** Returns true if field address1 is set (has been assigned a value) and false otherwise */
  public boolean isSetAddress1() {
    return this.address1 != null;
  }

  public void setAddress1IsSet(boolean value) {
    if (!value) {
      this.address1 = null;
    }
  }

  public String getAddress2() {
    return this.address2;
  }

  public Address_t setAddress2(String address2) {
    this.address2 = address2;
    return this;
  }

  public void unsetAddress2() {
    this.address2 = null;
  }

  /** Returns true if field address2 is set (has been assigned a value) and false otherwise */
  public boolean isSetAddress2() {
    return this.address2 != null;
  }

  public void setAddress2IsSet(boolean value) {
    if (!value) {
      this.address2 = null;
    }
  }

  public String getCity() {
    return this.city;
  }

  public Address_t setCity(String city) {
    this.city = city;
    return this;
  }

  public void unsetCity() {
    this.city = null;
  }

  /** Returns true if field city is set (has been assigned a value) and false otherwise */
  public boolean isSetCity() {
    return this.city != null;
  }

  public void setCityIsSet(boolean value) {
    if (!value) {
      this.city = null;
    }
  }

  public String getStateProvinceCounty() {
    return this.stateProvinceCounty;
  }

  public Address_t setStateProvinceCounty(String stateProvinceCounty) {
    this.stateProvinceCounty = stateProvinceCounty;
    return this;
  }

  public void unsetStateProvinceCounty() {
    this.stateProvinceCounty = null;
  }

  /** Returns true if field stateProvinceCounty is set (has been assigned a value) and false otherwise */
  public boolean isSetStateProvinceCounty() {
    return this.stateProvinceCounty != null;
  }

  public void setStateProvinceCountyIsSet(boolean value) {
    if (!value) {
      this.stateProvinceCounty = null;
    }
  }

  public String getZip() {
    return this.zip;
  }

  public Address_t setZip(String zip) {
    this.zip = zip;
    return this;
  }

  public void unsetZip() {
    this.zip = null;
  }

  /** Returns true if field zip is set (has been assigned a value) and false otherwise */
  public boolean isSetZip() {
    return this.zip != null;
  }

  public void setZipIsSet(boolean value) {
    if (!value) {
      this.zip = null;
    }
  }

  public String getCountry() {
    return this.country;
  }

  public Address_t setCountry(String country) {
    this.country = country;
    return this;
  }

  public void unsetCountry() {
    this.country = null;
  }

  /** Returns true if field country is set (has been assigned a value) and false otherwise */
  public boolean isSetCountry() {
    return this.country != null;
  }

  public void setCountryIsSet(boolean value) {
    if (!value) {
      this.country = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ADDRESS_ID:
      if (value == null) {
        unsetAddressId();
      } else {
        setAddressId((Long)value);
      }
      break;

    case ADDRESS1:
      if (value == null) {
        unsetAddress1();
      } else {
        setAddress1((String)value);
      }
      break;

    case ADDRESS2:
      if (value == null) {
        unsetAddress2();
      } else {
        setAddress2((String)value);
      }
      break;

    case CITY:
      if (value == null) {
        unsetCity();
      } else {
        setCity((String)value);
      }
      break;

    case STATE_PROVINCE_COUNTY:
      if (value == null) {
        unsetStateProvinceCounty();
      } else {
        setStateProvinceCounty((String)value);
      }
      break;

    case ZIP:
      if (value == null) {
        unsetZip();
      } else {
        setZip((String)value);
      }
      break;

    case COUNTRY:
      if (value == null) {
        unsetCountry();
      } else {
        setCountry((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ADDRESS_ID:
      return Long.valueOf(getAddressId());

    case ADDRESS1:
      return getAddress1();

    case ADDRESS2:
      return getAddress2();

    case CITY:
      return getCity();

    case STATE_PROVINCE_COUNTY:
      return getStateProvinceCounty();

    case ZIP:
      return getZip();

    case COUNTRY:
      return getCountry();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ADDRESS_ID:
      return isSetAddressId();
    case ADDRESS1:
      return isSetAddress1();
    case ADDRESS2:
      return isSetAddress2();
    case CITY:
      return isSetCity();
    case STATE_PROVINCE_COUNTY:
      return isSetStateProvinceCounty();
    case ZIP:
      return isSetZip();
    case COUNTRY:
      return isSetCountry();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Address_t)
      return this.equals((Address_t)that);
    return false;
  }

  public boolean equals(Address_t that) {
    if (that == null)
      return false;

    boolean this_present_addressId = true && this.isSetAddressId();
    boolean that_present_addressId = true && that.isSetAddressId();
    if (this_present_addressId || that_present_addressId) {
      if (!(this_present_addressId && that_present_addressId))
        return false;
      if (this.addressId != that.addressId)
        return false;
    }

    boolean this_present_address1 = true && this.isSetAddress1();
    boolean that_present_address1 = true && that.isSetAddress1();
    if (this_present_address1 || that_present_address1) {
      if (!(this_present_address1 && that_present_address1))
        return false;
      if (!this.address1.equals(that.address1))
        return false;
    }

    boolean this_present_address2 = true && this.isSetAddress2();
    boolean that_present_address2 = true && that.isSetAddress2();
    if (this_present_address2 || that_present_address2) {
      if (!(this_present_address2 && that_present_address2))
        return false;
      if (!this.address2.equals(that.address2))
        return false;
    }

    boolean this_present_city = true && this.isSetCity();
    boolean that_present_city = true && that.isSetCity();
    if (this_present_city || that_present_city) {
      if (!(this_present_city && that_present_city))
        return false;
      if (!this.city.equals(that.city))
        return false;
    }

    boolean this_present_stateProvinceCounty = true && this.isSetStateProvinceCounty();
    boolean that_present_stateProvinceCounty = true && that.isSetStateProvinceCounty();
    if (this_present_stateProvinceCounty || that_present_stateProvinceCounty) {
      if (!(this_present_stateProvinceCounty && that_present_stateProvinceCounty))
        return false;
      if (!this.stateProvinceCounty.equals(that.stateProvinceCounty))
        return false;
    }

    boolean this_present_zip = true && this.isSetZip();
    boolean that_present_zip = true && that.isSetZip();
    if (this_present_zip || that_present_zip) {
      if (!(this_present_zip && that_present_zip))
        return false;
      if (!this.zip.equals(that.zip))
        return false;
    }

    boolean this_present_country = true && this.isSetCountry();
    boolean that_present_country = true && that.isSetCountry();
    if (this_present_country || that_present_country) {
      if (!(this_present_country && that_present_country))
        return false;
      if (!this.country.equals(that.country))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(Address_t other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Address_t typedOther = (Address_t)other;

    lastComparison = Boolean.valueOf(isSetAddressId()).compareTo(typedOther.isSetAddressId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAddressId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.addressId, typedOther.addressId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAddress1()).compareTo(typedOther.isSetAddress1());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAddress1()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.address1, typedOther.address1);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAddress2()).compareTo(typedOther.isSetAddress2());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAddress2()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.address2, typedOther.address2);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCity()).compareTo(typedOther.isSetCity());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCity()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.city, typedOther.city);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStateProvinceCounty()).compareTo(typedOther.isSetStateProvinceCounty());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStateProvinceCounty()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.stateProvinceCounty, typedOther.stateProvinceCounty);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetZip()).compareTo(typedOther.isSetZip());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetZip()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.zip, typedOther.zip);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCountry()).compareTo(typedOther.isSetCountry());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCountry()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.country, typedOther.country);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Address_t(");
    boolean first = true;

    if (isSetAddressId()) {
      sb.append("addressId:");
      sb.append(this.addressId);
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("address1:");
    if (this.address1 == null) {
      sb.append("null");
    } else {
      sb.append(this.address1);
    }
    first = false;
    if (isSetAddress2()) {
      if (!first) sb.append(", ");
      sb.append("address2:");
      if (this.address2 == null) {
        sb.append("null");
      } else {
        sb.append(this.address2);
      }
      first = false;
    }
    if (isSetCity()) {
      if (!first) sb.append(", ");
      sb.append("city:");
      if (this.city == null) {
        sb.append("null");
      } else {
        sb.append(this.city);
      }
      first = false;
    }
    if (isSetStateProvinceCounty()) {
      if (!first) sb.append(", ");
      sb.append("stateProvinceCounty:");
      if (this.stateProvinceCounty == null) {
        sb.append("null");
      } else {
        sb.append(this.stateProvinceCounty);
      }
      first = false;
    }
    if (isSetZip()) {
      if (!first) sb.append(", ");
      sb.append("zip:");
      if (this.zip == null) {
        sb.append("null");
      } else {
        sb.append(this.zip);
      }
      first = false;
    }
    if (isSetCountry()) {
      if (!first) sb.append(", ");
      sb.append("country:");
      if (this.country == null) {
        sb.append("null");
      } else {
        sb.append(this.country);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (address1 == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'address1' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te.getMessage());
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te.getMessage());
    }
  }

  private static class Address_tStandardSchemeFactory implements SchemeFactory {
    public Address_tStandardScheme getScheme() {
      return new Address_tStandardScheme();
    }
  }

  private static class Address_tStandardScheme extends StandardScheme<Address_t> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Address_t struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ADDRESS_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.addressId = iprot.readI64();
              struct.setAddressIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ADDRESS1
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.address1 = iprot.readString();
              struct.setAddress1IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // ADDRESS2
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.address2 = iprot.readString();
              struct.setAddress2IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // CITY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.city = iprot.readString();
              struct.setCityIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // STATE_PROVINCE_COUNTY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.stateProvinceCounty = iprot.readString();
              struct.setStateProvinceCountyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // ZIP
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.zip = iprot.readString();
              struct.setZipIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // COUNTRY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.country = iprot.readString();
              struct.setCountryIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Address_t struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.isSetAddressId()) {
        oprot.writeFieldBegin(ADDRESS_ID_FIELD_DESC);
        oprot.writeI64(struct.addressId);
        oprot.writeFieldEnd();
      }
      if (struct.address1 != null) {
        oprot.writeFieldBegin(ADDRESS1_FIELD_DESC);
        oprot.writeString(struct.address1);
        oprot.writeFieldEnd();
      }
      if (struct.address2 != null) {
        if (struct.isSetAddress2()) {
          oprot.writeFieldBegin(ADDRESS2_FIELD_DESC);
          oprot.writeString(struct.address2);
          oprot.writeFieldEnd();
        }
      }
      if (struct.city != null) {
        if (struct.isSetCity()) {
          oprot.writeFieldBegin(CITY_FIELD_DESC);
          oprot.writeString(struct.city);
          oprot.writeFieldEnd();
        }
      }
      if (struct.stateProvinceCounty != null) {
        if (struct.isSetStateProvinceCounty()) {
          oprot.writeFieldBegin(STATE_PROVINCE_COUNTY_FIELD_DESC);
          oprot.writeString(struct.stateProvinceCounty);
          oprot.writeFieldEnd();
        }
      }
      if (struct.zip != null) {
        if (struct.isSetZip()) {
          oprot.writeFieldBegin(ZIP_FIELD_DESC);
          oprot.writeString(struct.zip);
          oprot.writeFieldEnd();
        }
      }
      if (struct.country != null) {
        if (struct.isSetCountry()) {
          oprot.writeFieldBegin(COUNTRY_FIELD_DESC);
          oprot.writeString(struct.country);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class Address_tTupleSchemeFactory implements SchemeFactory {
    public Address_tTupleScheme getScheme() {
      return new Address_tTupleScheme();
    }
  }

  private static class Address_tTupleScheme extends TupleScheme<Address_t> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Address_t struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.address1);
      BitSet optionals = new BitSet();
      if (struct.isSetAddressId()) {
        optionals.set(0);
      }
      if (struct.isSetAddress2()) {
        optionals.set(1);
      }
      if (struct.isSetCity()) {
        optionals.set(2);
      }
      if (struct.isSetStateProvinceCounty()) {
        optionals.set(3);
      }
      if (struct.isSetZip()) {
        optionals.set(4);
      }
      if (struct.isSetCountry()) {
        optionals.set(5);
      }
      oprot.writeBitSet(optionals, 6);
      if (struct.isSetAddressId()) {
        oprot.writeI64(struct.addressId);
      }
      if (struct.isSetAddress2()) {
        oprot.writeString(struct.address2);
      }
      if (struct.isSetCity()) {
        oprot.writeString(struct.city);
      }
      if (struct.isSetStateProvinceCounty()) {
        oprot.writeString(struct.stateProvinceCounty);
      }
      if (struct.isSetZip()) {
        oprot.writeString(struct.zip);
      }
      if (struct.isSetCountry()) {
        oprot.writeString(struct.country);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Address_t struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.address1 = iprot.readString();
      struct.setAddress1IsSet(true);
      BitSet incoming = iprot.readBitSet(6);
      if (incoming.get(0)) {
        struct.addressId = iprot.readI64();
        struct.setAddressIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.address2 = iprot.readString();
        struct.setAddress2IsSet(true);
      }
      if (incoming.get(2)) {
        struct.city = iprot.readString();
        struct.setCityIsSet(true);
      }
      if (incoming.get(3)) {
        struct.stateProvinceCounty = iprot.readString();
        struct.setStateProvinceCountyIsSet(true);
      }
      if (incoming.get(4)) {
        struct.zip = iprot.readString();
        struct.setZipIsSet(true);
      }
      if (incoming.get(5)) {
        struct.country = iprot.readString();
        struct.setCountryIsSet(true);
      }
    }
  }

}

