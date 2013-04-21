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

public class SearchOptions_t implements org.apache.thrift.TBase<SearchOptions_t, SearchOptions_t._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SearchOptions_t");

  private static final org.apache.thrift.protocol.TField SORT_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("sortType", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField SORT_PROPERTY_FIELD_DESC = new org.apache.thrift.protocol.TField("sortProperty", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField FIRST_RESULT_FIELD_DESC = new org.apache.thrift.protocol.TField("firstResult", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField MAX_RESULTS_FIELD_DESC = new org.apache.thrift.protocol.TField("maxResults", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField PAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("page", org.apache.thrift.protocol.TType.I32, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new SearchOptions_tStandardSchemeFactory());
    schemes.put(TupleScheme.class, new SearchOptions_tTupleSchemeFactory());
  }

  /**
   * 
   * @see SortType_t
   */
  public SortType_t sortType; // required
  public String sortProperty; // required
  public int firstResult; // required
  public int maxResults; // required
  public int page; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see SortType_t
     */
    SORT_TYPE((short)1, "sortType"),
    SORT_PROPERTY((short)2, "sortProperty"),
    FIRST_RESULT((short)3, "firstResult"),
    MAX_RESULTS((short)4, "maxResults"),
    PAGE((short)5, "page");

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
        case 1: // SORT_TYPE
          return SORT_TYPE;
        case 2: // SORT_PROPERTY
          return SORT_PROPERTY;
        case 3: // FIRST_RESULT
          return FIRST_RESULT;
        case 4: // MAX_RESULTS
          return MAX_RESULTS;
        case 5: // PAGE
          return PAGE;
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
  private static final int __FIRSTRESULT_ISSET_ID = 0;
  private static final int __MAXRESULTS_ISSET_ID = 1;
  private static final int __PAGE_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SORT_TYPE, new org.apache.thrift.meta_data.FieldMetaData("sortType", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, SortType_t.class)));
    tmpMap.put(_Fields.SORT_PROPERTY, new org.apache.thrift.meta_data.FieldMetaData("sortProperty", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.FIRST_RESULT, new org.apache.thrift.meta_data.FieldMetaData("firstResult", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MAX_RESULTS, new org.apache.thrift.meta_data.FieldMetaData("maxResults", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.PAGE, new org.apache.thrift.meta_data.FieldMetaData("page", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SearchOptions_t.class, metaDataMap);
  }

  public SearchOptions_t() {
  }

  public SearchOptions_t(
    SortType_t sortType,
    String sortProperty,
    int firstResult,
    int maxResults,
    int page)
  {
    this();
    this.sortType = sortType;
    this.sortProperty = sortProperty;
    this.firstResult = firstResult;
    setFirstResultIsSet(true);
    this.maxResults = maxResults;
    setMaxResultsIsSet(true);
    this.page = page;
    setPageIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SearchOptions_t(SearchOptions_t other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetSortType()) {
      this.sortType = other.sortType;
    }
    if (other.isSetSortProperty()) {
      this.sortProperty = other.sortProperty;
    }
    this.firstResult = other.firstResult;
    this.maxResults = other.maxResults;
    this.page = other.page;
  }

  public SearchOptions_t deepCopy() {
    return new SearchOptions_t(this);
  }

  public void clear() {
    this.sortType = null;
    this.sortProperty = null;
    setFirstResultIsSet(false);
    this.firstResult = 0;
    setMaxResultsIsSet(false);
    this.maxResults = 0;
    setPageIsSet(false);
    this.page = 0;
  }

  /**
   * 
   * @see SortType_t
   */
  public SortType_t getSortType() {
    return this.sortType;
  }

  /**
   * 
   * @see SortType_t
   */
  public SearchOptions_t setSortType(SortType_t sortType) {
    this.sortType = sortType;
    return this;
  }

  public void unsetSortType() {
    this.sortType = null;
  }

  /** Returns true if field sortType is set (has been assigned a value) and false otherwise */
  public boolean isSetSortType() {
    return this.sortType != null;
  }

  public void setSortTypeIsSet(boolean value) {
    if (!value) {
      this.sortType = null;
    }
  }

  public String getSortProperty() {
    return this.sortProperty;
  }

  public SearchOptions_t setSortProperty(String sortProperty) {
    this.sortProperty = sortProperty;
    return this;
  }

  public void unsetSortProperty() {
    this.sortProperty = null;
  }

  /** Returns true if field sortProperty is set (has been assigned a value) and false otherwise */
  public boolean isSetSortProperty() {
    return this.sortProperty != null;
  }

  public void setSortPropertyIsSet(boolean value) {
    if (!value) {
      this.sortProperty = null;
    }
  }

  public int getFirstResult() {
    return this.firstResult;
  }

  public SearchOptions_t setFirstResult(int firstResult) {
    this.firstResult = firstResult;
    setFirstResultIsSet(true);
    return this;
  }

  public void unsetFirstResult() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __FIRSTRESULT_ISSET_ID);
  }

  /** Returns true if field firstResult is set (has been assigned a value) and false otherwise */
  public boolean isSetFirstResult() {
    return EncodingUtils.testBit(__isset_bitfield, __FIRSTRESULT_ISSET_ID);
  }

  public void setFirstResultIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __FIRSTRESULT_ISSET_ID, value);
  }

  public int getMaxResults() {
    return this.maxResults;
  }

  public SearchOptions_t setMaxResults(int maxResults) {
    this.maxResults = maxResults;
    setMaxResultsIsSet(true);
    return this;
  }

  public void unsetMaxResults() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __MAXRESULTS_ISSET_ID);
  }

  /** Returns true if field maxResults is set (has been assigned a value) and false otherwise */
  public boolean isSetMaxResults() {
    return EncodingUtils.testBit(__isset_bitfield, __MAXRESULTS_ISSET_ID);
  }

  public void setMaxResultsIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __MAXRESULTS_ISSET_ID, value);
  }

  public int getPage() {
    return this.page;
  }

  public SearchOptions_t setPage(int page) {
    this.page = page;
    setPageIsSet(true);
    return this;
  }

  public void unsetPage() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PAGE_ISSET_ID);
  }

  /** Returns true if field page is set (has been assigned a value) and false otherwise */
  public boolean isSetPage() {
    return EncodingUtils.testBit(__isset_bitfield, __PAGE_ISSET_ID);
  }

  public void setPageIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PAGE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case SORT_TYPE:
      if (value == null) {
        unsetSortType();
      } else {
        setSortType((SortType_t)value);
      }
      break;

    case SORT_PROPERTY:
      if (value == null) {
        unsetSortProperty();
      } else {
        setSortProperty((String)value);
      }
      break;

    case FIRST_RESULT:
      if (value == null) {
        unsetFirstResult();
      } else {
        setFirstResult((Integer)value);
      }
      break;

    case MAX_RESULTS:
      if (value == null) {
        unsetMaxResults();
      } else {
        setMaxResults((Integer)value);
      }
      break;

    case PAGE:
      if (value == null) {
        unsetPage();
      } else {
        setPage((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case SORT_TYPE:
      return getSortType();

    case SORT_PROPERTY:
      return getSortProperty();

    case FIRST_RESULT:
      return Integer.valueOf(getFirstResult());

    case MAX_RESULTS:
      return Integer.valueOf(getMaxResults());

    case PAGE:
      return Integer.valueOf(getPage());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case SORT_TYPE:
      return isSetSortType();
    case SORT_PROPERTY:
      return isSetSortProperty();
    case FIRST_RESULT:
      return isSetFirstResult();
    case MAX_RESULTS:
      return isSetMaxResults();
    case PAGE:
      return isSetPage();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SearchOptions_t)
      return this.equals((SearchOptions_t)that);
    return false;
  }

  public boolean equals(SearchOptions_t that) {
    if (that == null)
      return false;

    boolean this_present_sortType = true && this.isSetSortType();
    boolean that_present_sortType = true && that.isSetSortType();
    if (this_present_sortType || that_present_sortType) {
      if (!(this_present_sortType && that_present_sortType))
        return false;
      if (!this.sortType.equals(that.sortType))
        return false;
    }

    boolean this_present_sortProperty = true && this.isSetSortProperty();
    boolean that_present_sortProperty = true && that.isSetSortProperty();
    if (this_present_sortProperty || that_present_sortProperty) {
      if (!(this_present_sortProperty && that_present_sortProperty))
        return false;
      if (!this.sortProperty.equals(that.sortProperty))
        return false;
    }

    boolean this_present_firstResult = true;
    boolean that_present_firstResult = true;
    if (this_present_firstResult || that_present_firstResult) {
      if (!(this_present_firstResult && that_present_firstResult))
        return false;
      if (this.firstResult != that.firstResult)
        return false;
    }

    boolean this_present_maxResults = true;
    boolean that_present_maxResults = true;
    if (this_present_maxResults || that_present_maxResults) {
      if (!(this_present_maxResults && that_present_maxResults))
        return false;
      if (this.maxResults != that.maxResults)
        return false;
    }

    boolean this_present_page = true;
    boolean that_present_page = true;
    if (this_present_page || that_present_page) {
      if (!(this_present_page && that_present_page))
        return false;
      if (this.page != that.page)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(SearchOptions_t other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    SearchOptions_t typedOther = (SearchOptions_t)other;

    lastComparison = Boolean.valueOf(isSetSortType()).compareTo(typedOther.isSetSortType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSortType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sortType, typedOther.sortType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSortProperty()).compareTo(typedOther.isSetSortProperty());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSortProperty()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sortProperty, typedOther.sortProperty);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFirstResult()).compareTo(typedOther.isSetFirstResult());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFirstResult()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.firstResult, typedOther.firstResult);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMaxResults()).compareTo(typedOther.isSetMaxResults());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMaxResults()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.maxResults, typedOther.maxResults);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPage()).compareTo(typedOther.isSetPage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.page, typedOther.page);
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
    StringBuilder sb = new StringBuilder("SearchOptions_t(");
    boolean first = true;

    sb.append("sortType:");
    if (this.sortType == null) {
      sb.append("null");
    } else {
      sb.append(this.sortType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("sortProperty:");
    if (this.sortProperty == null) {
      sb.append("null");
    } else {
      sb.append(this.sortProperty);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("firstResult:");
    sb.append(this.firstResult);
    first = false;
    if (!first) sb.append(", ");
    sb.append("maxResults:");
    sb.append(this.maxResults);
    first = false;
    if (!first) sb.append(", ");
    sb.append("page:");
    sb.append(this.page);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (sortType == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'sortType' was not present! Struct: " + toString());
    }
    if (sortProperty == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'sortProperty' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'firstResult' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'maxResults' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'page' because it's a primitive and you chose the non-beans generator.
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

  private static class SearchOptions_tStandardSchemeFactory implements SchemeFactory {
    public SearchOptions_tStandardScheme getScheme() {
      return new SearchOptions_tStandardScheme();
    }
  }

  private static class SearchOptions_tStandardScheme extends StandardScheme<SearchOptions_t> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SearchOptions_t struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SORT_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.sortType = SortType_t.findByValue(iprot.readI32());
              struct.setSortTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SORT_PROPERTY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.sortProperty = iprot.readString();
              struct.setSortPropertyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // FIRST_RESULT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.firstResult = iprot.readI32();
              struct.setFirstResultIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // MAX_RESULTS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.maxResults = iprot.readI32();
              struct.setMaxResultsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // PAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.page = iprot.readI32();
              struct.setPageIsSet(true);
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
      if (!struct.isSetFirstResult()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'firstResult' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetMaxResults()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'maxResults' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetPage()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'page' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, SearchOptions_t struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.sortType != null) {
        oprot.writeFieldBegin(SORT_TYPE_FIELD_DESC);
        oprot.writeI32(struct.sortType.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.sortProperty != null) {
        oprot.writeFieldBegin(SORT_PROPERTY_FIELD_DESC);
        oprot.writeString(struct.sortProperty);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(FIRST_RESULT_FIELD_DESC);
      oprot.writeI32(struct.firstResult);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(MAX_RESULTS_FIELD_DESC);
      oprot.writeI32(struct.maxResults);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(PAGE_FIELD_DESC);
      oprot.writeI32(struct.page);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SearchOptions_tTupleSchemeFactory implements SchemeFactory {
    public SearchOptions_tTupleScheme getScheme() {
      return new SearchOptions_tTupleScheme();
    }
  }

  private static class SearchOptions_tTupleScheme extends TupleScheme<SearchOptions_t> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SearchOptions_t struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.sortType.getValue());
      oprot.writeString(struct.sortProperty);
      oprot.writeI32(struct.firstResult);
      oprot.writeI32(struct.maxResults);
      oprot.writeI32(struct.page);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SearchOptions_t struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.sortType = SortType_t.findByValue(iprot.readI32());
      struct.setSortTypeIsSet(true);
      struct.sortProperty = iprot.readString();
      struct.setSortPropertyIsSet(true);
      struct.firstResult = iprot.readI32();
      struct.setFirstResultIsSet(true);
      struct.maxResults = iprot.readI32();
      struct.setMaxResultsIsSet(true);
      struct.page = iprot.readI32();
      struct.setPageIsSet(true);
    }
  }

}

