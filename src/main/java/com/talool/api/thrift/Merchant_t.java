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

public class Merchant_t implements org.apache.thrift.TBase<Merchant_t, Merchant_t._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Merchant_t");

  private static final org.apache.thrift.protocol.TField MERCHANT_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("merchantId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField EMAIL_FIELD_DESC = new org.apache.thrift.protocol.TField("email", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField WEBSITE_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("websiteUrl", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField LOGO_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("logoUrl", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField PHONE_FIELD_DESC = new org.apache.thrift.protocol.TField("phone", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField ADDRESS_FIELD_DESC = new org.apache.thrift.protocol.TField("address", org.apache.thrift.protocol.TType.STRUCT, (short)7);
  private static final org.apache.thrift.protocol.TField CREATED_FIELD_DESC = new org.apache.thrift.protocol.TField("created", org.apache.thrift.protocol.TType.I64, (short)20);
  private static final org.apache.thrift.protocol.TField UPDATED_FIELD_DESC = new org.apache.thrift.protocol.TField("updated", org.apache.thrift.protocol.TType.I64, (short)21);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new Merchant_tStandardSchemeFactory());
    schemes.put(TupleScheme.class, new Merchant_tTupleSchemeFactory());
  }

  public long merchantId; // optional
  public String name; // required
  public String email; // optional
  public String websiteUrl; // optional
  public String logoUrl; // optional
  public String phone; // optional
  public Address_t address; // optional
  public long created; // optional
  public long updated; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MERCHANT_ID((short)1, "merchantId"),
    NAME((short)2, "name"),
    EMAIL((short)3, "email"),
    WEBSITE_URL((short)4, "websiteUrl"),
    LOGO_URL((short)5, "logoUrl"),
    PHONE((short)6, "phone"),
    ADDRESS((short)7, "address"),
    CREATED((short)20, "created"),
    UPDATED((short)21, "updated");

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
        case 1: // MERCHANT_ID
          return MERCHANT_ID;
        case 2: // NAME
          return NAME;
        case 3: // EMAIL
          return EMAIL;
        case 4: // WEBSITE_URL
          return WEBSITE_URL;
        case 5: // LOGO_URL
          return LOGO_URL;
        case 6: // PHONE
          return PHONE;
        case 7: // ADDRESS
          return ADDRESS;
        case 20: // CREATED
          return CREATED;
        case 21: // UPDATED
          return UPDATED;
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
  private static final int __MERCHANTID_ISSET_ID = 0;
  private static final int __CREATED_ISSET_ID = 1;
  private static final int __UPDATED_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.MERCHANT_ID,_Fields.EMAIL,_Fields.WEBSITE_URL,_Fields.LOGO_URL,_Fields.PHONE,_Fields.ADDRESS,_Fields.CREATED,_Fields.UPDATED};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MERCHANT_ID, new org.apache.thrift.meta_data.FieldMetaData("merchantId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.EMAIL, new org.apache.thrift.meta_data.FieldMetaData("email", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.WEBSITE_URL, new org.apache.thrift.meta_data.FieldMetaData("websiteUrl", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.LOGO_URL, new org.apache.thrift.meta_data.FieldMetaData("logoUrl", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PHONE, new org.apache.thrift.meta_data.FieldMetaData("phone", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ADDRESS, new org.apache.thrift.meta_data.FieldMetaData("address", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Address_t.class)));
    tmpMap.put(_Fields.CREATED, new org.apache.thrift.meta_data.FieldMetaData("created", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    tmpMap.put(_Fields.UPDATED, new org.apache.thrift.meta_data.FieldMetaData("updated", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Merchant_t.class, metaDataMap);
  }

  public Merchant_t() {
  }

  public Merchant_t(
    String name)
  {
    this();
    this.name = name;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Merchant_t(Merchant_t other) {
    __isset_bitfield = other.__isset_bitfield;
    this.merchantId = other.merchantId;
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetEmail()) {
      this.email = other.email;
    }
    if (other.isSetWebsiteUrl()) {
      this.websiteUrl = other.websiteUrl;
    }
    if (other.isSetLogoUrl()) {
      this.logoUrl = other.logoUrl;
    }
    if (other.isSetPhone()) {
      this.phone = other.phone;
    }
    if (other.isSetAddress()) {
      this.address = new Address_t(other.address);
    }
    this.created = other.created;
    this.updated = other.updated;
  }

  public Merchant_t deepCopy() {
    return new Merchant_t(this);
  }

  public void clear() {
    setMerchantIdIsSet(false);
    this.merchantId = 0;
    this.name = null;
    this.email = null;
    this.websiteUrl = null;
    this.logoUrl = null;
    this.phone = null;
    this.address = null;
    setCreatedIsSet(false);
    this.created = 0;
    setUpdatedIsSet(false);
    this.updated = 0;
  }

  public long getMerchantId() {
    return this.merchantId;
  }

  public Merchant_t setMerchantId(long merchantId) {
    this.merchantId = merchantId;
    setMerchantIdIsSet(true);
    return this;
  }

  public void unsetMerchantId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __MERCHANTID_ISSET_ID);
  }

  /** Returns true if field merchantId is set (has been assigned a value) and false otherwise */
  public boolean isSetMerchantId() {
    return EncodingUtils.testBit(__isset_bitfield, __MERCHANTID_ISSET_ID);
  }

  public void setMerchantIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __MERCHANTID_ISSET_ID, value);
  }

  public String getName() {
    return this.name;
  }

  public Merchant_t setName(String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public String getEmail() {
    return this.email;
  }

  public Merchant_t setEmail(String email) {
    this.email = email;
    return this;
  }

  public void unsetEmail() {
    this.email = null;
  }

  /** Returns true if field email is set (has been assigned a value) and false otherwise */
  public boolean isSetEmail() {
    return this.email != null;
  }

  public void setEmailIsSet(boolean value) {
    if (!value) {
      this.email = null;
    }
  }

  public String getWebsiteUrl() {
    return this.websiteUrl;
  }

  public Merchant_t setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
    return this;
  }

  public void unsetWebsiteUrl() {
    this.websiteUrl = null;
  }

  /** Returns true if field websiteUrl is set (has been assigned a value) and false otherwise */
  public boolean isSetWebsiteUrl() {
    return this.websiteUrl != null;
  }

  public void setWebsiteUrlIsSet(boolean value) {
    if (!value) {
      this.websiteUrl = null;
    }
  }

  public String getLogoUrl() {
    return this.logoUrl;
  }

  public Merchant_t setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
    return this;
  }

  public void unsetLogoUrl() {
    this.logoUrl = null;
  }

  /** Returns true if field logoUrl is set (has been assigned a value) and false otherwise */
  public boolean isSetLogoUrl() {
    return this.logoUrl != null;
  }

  public void setLogoUrlIsSet(boolean value) {
    if (!value) {
      this.logoUrl = null;
    }
  }

  public String getPhone() {
    return this.phone;
  }

  public Merchant_t setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public void unsetPhone() {
    this.phone = null;
  }

  /** Returns true if field phone is set (has been assigned a value) and false otherwise */
  public boolean isSetPhone() {
    return this.phone != null;
  }

  public void setPhoneIsSet(boolean value) {
    if (!value) {
      this.phone = null;
    }
  }

  public Address_t getAddress() {
    return this.address;
  }

  public Merchant_t setAddress(Address_t address) {
    this.address = address;
    return this;
  }

  public void unsetAddress() {
    this.address = null;
  }

  /** Returns true if field address is set (has been assigned a value) and false otherwise */
  public boolean isSetAddress() {
    return this.address != null;
  }

  public void setAddressIsSet(boolean value) {
    if (!value) {
      this.address = null;
    }
  }

  public long getCreated() {
    return this.created;
  }

  public Merchant_t setCreated(long created) {
    this.created = created;
    setCreatedIsSet(true);
    return this;
  }

  public void unsetCreated() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __CREATED_ISSET_ID);
  }

  /** Returns true if field created is set (has been assigned a value) and false otherwise */
  public boolean isSetCreated() {
    return EncodingUtils.testBit(__isset_bitfield, __CREATED_ISSET_ID);
  }

  public void setCreatedIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __CREATED_ISSET_ID, value);
  }

  public long getUpdated() {
    return this.updated;
  }

  public Merchant_t setUpdated(long updated) {
    this.updated = updated;
    setUpdatedIsSet(true);
    return this;
  }

  public void unsetUpdated() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __UPDATED_ISSET_ID);
  }

  /** Returns true if field updated is set (has been assigned a value) and false otherwise */
  public boolean isSetUpdated() {
    return EncodingUtils.testBit(__isset_bitfield, __UPDATED_ISSET_ID);
  }

  public void setUpdatedIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __UPDATED_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case MERCHANT_ID:
      if (value == null) {
        unsetMerchantId();
      } else {
        setMerchantId((Long)value);
      }
      break;

    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case EMAIL:
      if (value == null) {
        unsetEmail();
      } else {
        setEmail((String)value);
      }
      break;

    case WEBSITE_URL:
      if (value == null) {
        unsetWebsiteUrl();
      } else {
        setWebsiteUrl((String)value);
      }
      break;

    case LOGO_URL:
      if (value == null) {
        unsetLogoUrl();
      } else {
        setLogoUrl((String)value);
      }
      break;

    case PHONE:
      if (value == null) {
        unsetPhone();
      } else {
        setPhone((String)value);
      }
      break;

    case ADDRESS:
      if (value == null) {
        unsetAddress();
      } else {
        setAddress((Address_t)value);
      }
      break;

    case CREATED:
      if (value == null) {
        unsetCreated();
      } else {
        setCreated((Long)value);
      }
      break;

    case UPDATED:
      if (value == null) {
        unsetUpdated();
      } else {
        setUpdated((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MERCHANT_ID:
      return Long.valueOf(getMerchantId());

    case NAME:
      return getName();

    case EMAIL:
      return getEmail();

    case WEBSITE_URL:
      return getWebsiteUrl();

    case LOGO_URL:
      return getLogoUrl();

    case PHONE:
      return getPhone();

    case ADDRESS:
      return getAddress();

    case CREATED:
      return Long.valueOf(getCreated());

    case UPDATED:
      return Long.valueOf(getUpdated());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MERCHANT_ID:
      return isSetMerchantId();
    case NAME:
      return isSetName();
    case EMAIL:
      return isSetEmail();
    case WEBSITE_URL:
      return isSetWebsiteUrl();
    case LOGO_URL:
      return isSetLogoUrl();
    case PHONE:
      return isSetPhone();
    case ADDRESS:
      return isSetAddress();
    case CREATED:
      return isSetCreated();
    case UPDATED:
      return isSetUpdated();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Merchant_t)
      return this.equals((Merchant_t)that);
    return false;
  }

  public boolean equals(Merchant_t that) {
    if (that == null)
      return false;

    boolean this_present_merchantId = true && this.isSetMerchantId();
    boolean that_present_merchantId = true && that.isSetMerchantId();
    if (this_present_merchantId || that_present_merchantId) {
      if (!(this_present_merchantId && that_present_merchantId))
        return false;
      if (this.merchantId != that.merchantId)
        return false;
    }

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_email = true && this.isSetEmail();
    boolean that_present_email = true && that.isSetEmail();
    if (this_present_email || that_present_email) {
      if (!(this_present_email && that_present_email))
        return false;
      if (!this.email.equals(that.email))
        return false;
    }

    boolean this_present_websiteUrl = true && this.isSetWebsiteUrl();
    boolean that_present_websiteUrl = true && that.isSetWebsiteUrl();
    if (this_present_websiteUrl || that_present_websiteUrl) {
      if (!(this_present_websiteUrl && that_present_websiteUrl))
        return false;
      if (!this.websiteUrl.equals(that.websiteUrl))
        return false;
    }

    boolean this_present_logoUrl = true && this.isSetLogoUrl();
    boolean that_present_logoUrl = true && that.isSetLogoUrl();
    if (this_present_logoUrl || that_present_logoUrl) {
      if (!(this_present_logoUrl && that_present_logoUrl))
        return false;
      if (!this.logoUrl.equals(that.logoUrl))
        return false;
    }

    boolean this_present_phone = true && this.isSetPhone();
    boolean that_present_phone = true && that.isSetPhone();
    if (this_present_phone || that_present_phone) {
      if (!(this_present_phone && that_present_phone))
        return false;
      if (!this.phone.equals(that.phone))
        return false;
    }

    boolean this_present_address = true && this.isSetAddress();
    boolean that_present_address = true && that.isSetAddress();
    if (this_present_address || that_present_address) {
      if (!(this_present_address && that_present_address))
        return false;
      if (!this.address.equals(that.address))
        return false;
    }

    boolean this_present_created = true && this.isSetCreated();
    boolean that_present_created = true && that.isSetCreated();
    if (this_present_created || that_present_created) {
      if (!(this_present_created && that_present_created))
        return false;
      if (this.created != that.created)
        return false;
    }

    boolean this_present_updated = true && this.isSetUpdated();
    boolean that_present_updated = true && that.isSetUpdated();
    if (this_present_updated || that_present_updated) {
      if (!(this_present_updated && that_present_updated))
        return false;
      if (this.updated != that.updated)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(Merchant_t other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Merchant_t typedOther = (Merchant_t)other;

    lastComparison = Boolean.valueOf(isSetMerchantId()).compareTo(typedOther.isSetMerchantId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMerchantId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.merchantId, typedOther.merchantId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetName()).compareTo(typedOther.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, typedOther.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEmail()).compareTo(typedOther.isSetEmail());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEmail()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.email, typedOther.email);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetWebsiteUrl()).compareTo(typedOther.isSetWebsiteUrl());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetWebsiteUrl()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.websiteUrl, typedOther.websiteUrl);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLogoUrl()).compareTo(typedOther.isSetLogoUrl());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLogoUrl()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.logoUrl, typedOther.logoUrl);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPhone()).compareTo(typedOther.isSetPhone());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPhone()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.phone, typedOther.phone);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAddress()).compareTo(typedOther.isSetAddress());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAddress()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.address, typedOther.address);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCreated()).compareTo(typedOther.isSetCreated());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCreated()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.created, typedOther.created);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUpdated()).compareTo(typedOther.isSetUpdated());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUpdated()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.updated, typedOther.updated);
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
    StringBuilder sb = new StringBuilder("Merchant_t(");
    boolean first = true;

    if (isSetMerchantId()) {
      sb.append("merchantId:");
      sb.append(this.merchantId);
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (isSetEmail()) {
      if (!first) sb.append(", ");
      sb.append("email:");
      if (this.email == null) {
        sb.append("null");
      } else {
        sb.append(this.email);
      }
      first = false;
    }
    if (isSetWebsiteUrl()) {
      if (!first) sb.append(", ");
      sb.append("websiteUrl:");
      if (this.websiteUrl == null) {
        sb.append("null");
      } else {
        sb.append(this.websiteUrl);
      }
      first = false;
    }
    if (isSetLogoUrl()) {
      if (!first) sb.append(", ");
      sb.append("logoUrl:");
      if (this.logoUrl == null) {
        sb.append("null");
      } else {
        sb.append(this.logoUrl);
      }
      first = false;
    }
    if (isSetPhone()) {
      if (!first) sb.append(", ");
      sb.append("phone:");
      if (this.phone == null) {
        sb.append("null");
      } else {
        sb.append(this.phone);
      }
      first = false;
    }
    if (isSetAddress()) {
      if (!first) sb.append(", ");
      sb.append("address:");
      if (this.address == null) {
        sb.append("null");
      } else {
        sb.append(this.address);
      }
      first = false;
    }
    if (isSetCreated()) {
      if (!first) sb.append(", ");
      sb.append("created:");
      sb.append(this.created);
      first = false;
    }
    if (isSetUpdated()) {
      if (!first) sb.append(", ");
      sb.append("updated:");
      sb.append(this.updated);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (name == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'name' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (address != null) {
      address.validate();
    }
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

  private static class Merchant_tStandardSchemeFactory implements SchemeFactory {
    public Merchant_tStandardScheme getScheme() {
      return new Merchant_tStandardScheme();
    }
  }

  private static class Merchant_tStandardScheme extends StandardScheme<Merchant_t> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Merchant_t struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MERCHANT_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.merchantId = iprot.readI64();
              struct.setMerchantIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // EMAIL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.email = iprot.readString();
              struct.setEmailIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // WEBSITE_URL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.websiteUrl = iprot.readString();
              struct.setWebsiteUrlIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // LOGO_URL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.logoUrl = iprot.readString();
              struct.setLogoUrlIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // PHONE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.phone = iprot.readString();
              struct.setPhoneIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // ADDRESS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.address = new Address_t();
              struct.address.read(iprot);
              struct.setAddressIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 20: // CREATED
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.created = iprot.readI64();
              struct.setCreatedIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 21: // UPDATED
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.updated = iprot.readI64();
              struct.setUpdatedIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Merchant_t struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.isSetMerchantId()) {
        oprot.writeFieldBegin(MERCHANT_ID_FIELD_DESC);
        oprot.writeI64(struct.merchantId);
        oprot.writeFieldEnd();
      }
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      if (struct.email != null) {
        if (struct.isSetEmail()) {
          oprot.writeFieldBegin(EMAIL_FIELD_DESC);
          oprot.writeString(struct.email);
          oprot.writeFieldEnd();
        }
      }
      if (struct.websiteUrl != null) {
        if (struct.isSetWebsiteUrl()) {
          oprot.writeFieldBegin(WEBSITE_URL_FIELD_DESC);
          oprot.writeString(struct.websiteUrl);
          oprot.writeFieldEnd();
        }
      }
      if (struct.logoUrl != null) {
        if (struct.isSetLogoUrl()) {
          oprot.writeFieldBegin(LOGO_URL_FIELD_DESC);
          oprot.writeString(struct.logoUrl);
          oprot.writeFieldEnd();
        }
      }
      if (struct.phone != null) {
        if (struct.isSetPhone()) {
          oprot.writeFieldBegin(PHONE_FIELD_DESC);
          oprot.writeString(struct.phone);
          oprot.writeFieldEnd();
        }
      }
      if (struct.address != null) {
        if (struct.isSetAddress()) {
          oprot.writeFieldBegin(ADDRESS_FIELD_DESC);
          struct.address.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetCreated()) {
        oprot.writeFieldBegin(CREATED_FIELD_DESC);
        oprot.writeI64(struct.created);
        oprot.writeFieldEnd();
      }
      if (struct.isSetUpdated()) {
        oprot.writeFieldBegin(UPDATED_FIELD_DESC);
        oprot.writeI64(struct.updated);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class Merchant_tTupleSchemeFactory implements SchemeFactory {
    public Merchant_tTupleScheme getScheme() {
      return new Merchant_tTupleScheme();
    }
  }

  private static class Merchant_tTupleScheme extends TupleScheme<Merchant_t> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Merchant_t struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.name);
      BitSet optionals = new BitSet();
      if (struct.isSetMerchantId()) {
        optionals.set(0);
      }
      if (struct.isSetEmail()) {
        optionals.set(1);
      }
      if (struct.isSetWebsiteUrl()) {
        optionals.set(2);
      }
      if (struct.isSetLogoUrl()) {
        optionals.set(3);
      }
      if (struct.isSetPhone()) {
        optionals.set(4);
      }
      if (struct.isSetAddress()) {
        optionals.set(5);
      }
      if (struct.isSetCreated()) {
        optionals.set(6);
      }
      if (struct.isSetUpdated()) {
        optionals.set(7);
      }
      oprot.writeBitSet(optionals, 8);
      if (struct.isSetMerchantId()) {
        oprot.writeI64(struct.merchantId);
      }
      if (struct.isSetEmail()) {
        oprot.writeString(struct.email);
      }
      if (struct.isSetWebsiteUrl()) {
        oprot.writeString(struct.websiteUrl);
      }
      if (struct.isSetLogoUrl()) {
        oprot.writeString(struct.logoUrl);
      }
      if (struct.isSetPhone()) {
        oprot.writeString(struct.phone);
      }
      if (struct.isSetAddress()) {
        struct.address.write(oprot);
      }
      if (struct.isSetCreated()) {
        oprot.writeI64(struct.created);
      }
      if (struct.isSetUpdated()) {
        oprot.writeI64(struct.updated);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Merchant_t struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.name = iprot.readString();
      struct.setNameIsSet(true);
      BitSet incoming = iprot.readBitSet(8);
      if (incoming.get(0)) {
        struct.merchantId = iprot.readI64();
        struct.setMerchantIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.email = iprot.readString();
        struct.setEmailIsSet(true);
      }
      if (incoming.get(2)) {
        struct.websiteUrl = iprot.readString();
        struct.setWebsiteUrlIsSet(true);
      }
      if (incoming.get(3)) {
        struct.logoUrl = iprot.readString();
        struct.setLogoUrlIsSet(true);
      }
      if (incoming.get(4)) {
        struct.phone = iprot.readString();
        struct.setPhoneIsSet(true);
      }
      if (incoming.get(5)) {
        struct.address = new Address_t();
        struct.address.read(iprot);
        struct.setAddressIsSet(true);
      }
      if (incoming.get(6)) {
        struct.created = iprot.readI64();
        struct.setCreatedIsSet(true);
      }
      if (incoming.get(7)) {
        struct.updated = iprot.readI64();
        struct.setUpdatedIsSet(true);
      }
    }
  }

}
