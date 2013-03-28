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

public class SocialAccount_t implements org.apache.thrift.TBase<SocialAccount_t, SocialAccount_t._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SocialAccount_t");

  private static final org.apache.thrift.protocol.TField SOCAL_NETWORK_FIELD_DESC = new org.apache.thrift.protocol.TField("socalNetwork", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField LOGIN_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("loginId", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField TOKEN_FIELD_DESC = new org.apache.thrift.protocol.TField("token", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField CREATED_FIELD_DESC = new org.apache.thrift.protocol.TField("created", org.apache.thrift.protocol.TType.I64, (short)20);
  private static final org.apache.thrift.protocol.TField UPDATED_FIELD_DESC = new org.apache.thrift.protocol.TField("updated", org.apache.thrift.protocol.TType.I64, (short)21);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new SocialAccount_tStandardSchemeFactory());
    schemes.put(TupleScheme.class, new SocialAccount_tTupleSchemeFactory());
  }

  /**
   * 
   * @see SocialNetwork_t
   */
  public SocialNetwork_t socalNetwork; // required
  public String loginId; // required
  public String token; // optional
  public long created; // optional
  public long updated; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see SocialNetwork_t
     */
    SOCAL_NETWORK((short)1, "socalNetwork"),
    LOGIN_ID((short)2, "loginId"),
    TOKEN((short)3, "token"),
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
        case 1: // SOCAL_NETWORK
          return SOCAL_NETWORK;
        case 2: // LOGIN_ID
          return LOGIN_ID;
        case 3: // TOKEN
          return TOKEN;
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
  private static final int __CREATED_ISSET_ID = 0;
  private static final int __UPDATED_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.TOKEN,_Fields.CREATED,_Fields.UPDATED};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SOCAL_NETWORK, new org.apache.thrift.meta_data.FieldMetaData("socalNetwork", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, SocialNetwork_t.class)));
    tmpMap.put(_Fields.LOGIN_ID, new org.apache.thrift.meta_data.FieldMetaData("loginId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TOKEN, new org.apache.thrift.meta_data.FieldMetaData("token", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CREATED, new org.apache.thrift.meta_data.FieldMetaData("created", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    tmpMap.put(_Fields.UPDATED, new org.apache.thrift.meta_data.FieldMetaData("updated", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SocialAccount_t.class, metaDataMap);
  }

  public SocialAccount_t() {
  }

  public SocialAccount_t(
    SocialNetwork_t socalNetwork,
    String loginId)
  {
    this();
    this.socalNetwork = socalNetwork;
    this.loginId = loginId;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SocialAccount_t(SocialAccount_t other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetSocalNetwork()) {
      this.socalNetwork = other.socalNetwork;
    }
    if (other.isSetLoginId()) {
      this.loginId = other.loginId;
    }
    if (other.isSetToken()) {
      this.token = other.token;
    }
    this.created = other.created;
    this.updated = other.updated;
  }

  public SocialAccount_t deepCopy() {
    return new SocialAccount_t(this);
  }

  public void clear() {
    this.socalNetwork = null;
    this.loginId = null;
    this.token = null;
    setCreatedIsSet(false);
    this.created = 0;
    setUpdatedIsSet(false);
    this.updated = 0;
  }

  /**
   * 
   * @see SocialNetwork_t
   */
  public SocialNetwork_t getSocalNetwork() {
    return this.socalNetwork;
  }

  /**
   * 
   * @see SocialNetwork_t
   */
  public SocialAccount_t setSocalNetwork(SocialNetwork_t socalNetwork) {
    this.socalNetwork = socalNetwork;
    return this;
  }

  public void unsetSocalNetwork() {
    this.socalNetwork = null;
  }

  /** Returns true if field socalNetwork is set (has been assigned a value) and false otherwise */
  public boolean isSetSocalNetwork() {
    return this.socalNetwork != null;
  }

  public void setSocalNetworkIsSet(boolean value) {
    if (!value) {
      this.socalNetwork = null;
    }
  }

  public String getLoginId() {
    return this.loginId;
  }

  public SocialAccount_t setLoginId(String loginId) {
    this.loginId = loginId;
    return this;
  }

  public void unsetLoginId() {
    this.loginId = null;
  }

  /** Returns true if field loginId is set (has been assigned a value) and false otherwise */
  public boolean isSetLoginId() {
    return this.loginId != null;
  }

  public void setLoginIdIsSet(boolean value) {
    if (!value) {
      this.loginId = null;
    }
  }

  public String getToken() {
    return this.token;
  }

  public SocialAccount_t setToken(String token) {
    this.token = token;
    return this;
  }

  public void unsetToken() {
    this.token = null;
  }

  /** Returns true if field token is set (has been assigned a value) and false otherwise */
  public boolean isSetToken() {
    return this.token != null;
  }

  public void setTokenIsSet(boolean value) {
    if (!value) {
      this.token = null;
    }
  }

  public long getCreated() {
    return this.created;
  }

  public SocialAccount_t setCreated(long created) {
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

  public SocialAccount_t setUpdated(long updated) {
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
    case SOCAL_NETWORK:
      if (value == null) {
        unsetSocalNetwork();
      } else {
        setSocalNetwork((SocialNetwork_t)value);
      }
      break;

    case LOGIN_ID:
      if (value == null) {
        unsetLoginId();
      } else {
        setLoginId((String)value);
      }
      break;

    case TOKEN:
      if (value == null) {
        unsetToken();
      } else {
        setToken((String)value);
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
    case SOCAL_NETWORK:
      return getSocalNetwork();

    case LOGIN_ID:
      return getLoginId();

    case TOKEN:
      return getToken();

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
    case SOCAL_NETWORK:
      return isSetSocalNetwork();
    case LOGIN_ID:
      return isSetLoginId();
    case TOKEN:
      return isSetToken();
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
    if (that instanceof SocialAccount_t)
      return this.equals((SocialAccount_t)that);
    return false;
  }

  public boolean equals(SocialAccount_t that) {
    if (that == null)
      return false;

    boolean this_present_socalNetwork = true && this.isSetSocalNetwork();
    boolean that_present_socalNetwork = true && that.isSetSocalNetwork();
    if (this_present_socalNetwork || that_present_socalNetwork) {
      if (!(this_present_socalNetwork && that_present_socalNetwork))
        return false;
      if (!this.socalNetwork.equals(that.socalNetwork))
        return false;
    }

    boolean this_present_loginId = true && this.isSetLoginId();
    boolean that_present_loginId = true && that.isSetLoginId();
    if (this_present_loginId || that_present_loginId) {
      if (!(this_present_loginId && that_present_loginId))
        return false;
      if (!this.loginId.equals(that.loginId))
        return false;
    }

    boolean this_present_token = true && this.isSetToken();
    boolean that_present_token = true && that.isSetToken();
    if (this_present_token || that_present_token) {
      if (!(this_present_token && that_present_token))
        return false;
      if (!this.token.equals(that.token))
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

  public int compareTo(SocialAccount_t other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    SocialAccount_t typedOther = (SocialAccount_t)other;

    lastComparison = Boolean.valueOf(isSetSocalNetwork()).compareTo(typedOther.isSetSocalNetwork());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSocalNetwork()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.socalNetwork, typedOther.socalNetwork);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLoginId()).compareTo(typedOther.isSetLoginId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLoginId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.loginId, typedOther.loginId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetToken()).compareTo(typedOther.isSetToken());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetToken()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.token, typedOther.token);
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
    StringBuilder sb = new StringBuilder("SocialAccount_t(");
    boolean first = true;

    sb.append("socalNetwork:");
    if (this.socalNetwork == null) {
      sb.append("null");
    } else {
      sb.append(this.socalNetwork);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("loginId:");
    if (this.loginId == null) {
      sb.append("null");
    } else {
      sb.append(this.loginId);
    }
    first = false;
    if (isSetToken()) {
      if (!first) sb.append(", ");
      sb.append("token:");
      if (this.token == null) {
        sb.append("null");
      } else {
        sb.append(this.token);
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
    if (socalNetwork == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'socalNetwork' was not present! Struct: " + toString());
    }
    if (loginId == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'loginId' was not present! Struct: " + toString());
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

  private static class SocialAccount_tStandardSchemeFactory implements SchemeFactory {
    public SocialAccount_tStandardScheme getScheme() {
      return new SocialAccount_tStandardScheme();
    }
  }

  private static class SocialAccount_tStandardScheme extends StandardScheme<SocialAccount_t> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SocialAccount_t struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SOCAL_NETWORK
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.socalNetwork = SocialNetwork_t.findByValue(iprot.readI32());
              struct.setSocalNetworkIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // LOGIN_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.loginId = iprot.readString();
              struct.setLoginIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TOKEN
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.token = iprot.readString();
              struct.setTokenIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, SocialAccount_t struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.socalNetwork != null) {
        oprot.writeFieldBegin(SOCAL_NETWORK_FIELD_DESC);
        oprot.writeI32(struct.socalNetwork.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.loginId != null) {
        oprot.writeFieldBegin(LOGIN_ID_FIELD_DESC);
        oprot.writeString(struct.loginId);
        oprot.writeFieldEnd();
      }
      if (struct.token != null) {
        if (struct.isSetToken()) {
          oprot.writeFieldBegin(TOKEN_FIELD_DESC);
          oprot.writeString(struct.token);
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

  private static class SocialAccount_tTupleSchemeFactory implements SchemeFactory {
    public SocialAccount_tTupleScheme getScheme() {
      return new SocialAccount_tTupleScheme();
    }
  }

  private static class SocialAccount_tTupleScheme extends TupleScheme<SocialAccount_t> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SocialAccount_t struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.socalNetwork.getValue());
      oprot.writeString(struct.loginId);
      BitSet optionals = new BitSet();
      if (struct.isSetToken()) {
        optionals.set(0);
      }
      if (struct.isSetCreated()) {
        optionals.set(1);
      }
      if (struct.isSetUpdated()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetToken()) {
        oprot.writeString(struct.token);
      }
      if (struct.isSetCreated()) {
        oprot.writeI64(struct.created);
      }
      if (struct.isSetUpdated()) {
        oprot.writeI64(struct.updated);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SocialAccount_t struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.socalNetwork = SocialNetwork_t.findByValue(iprot.readI32());
      struct.setSocalNetworkIsSet(true);
      struct.loginId = iprot.readString();
      struct.setLoginIdIsSet(true);
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.token = iprot.readString();
        struct.setTokenIsSet(true);
      }
      if (incoming.get(1)) {
        struct.created = iprot.readI64();
        struct.setCreatedIsSet(true);
      }
      if (incoming.get(2)) {
        struct.updated = iprot.readI64();
        struct.setUpdatedIsSet(true);
      }
    }
  }

}
