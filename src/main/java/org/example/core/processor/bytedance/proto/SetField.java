// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: stmt.proto

package org.example.core.processor.bytedance.proto;

/**
 * <pre>
 * SetField object-&gt;field = value
 * </pre>
 *
 * Protobuf type {@code security.sast.codegraph.repopb.SetField}
 */
public final class SetField extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:security.sast.codegraph.repopb.SetField)
    SetFieldOrBuilder {
private static final long serialVersionUID = 0L;
  // Use SetField.newBuilder() to construct.
  private SetField(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SetField() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new SetField();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.example.core.processor.bytedance.proto.CodeGraphStatement.internal_static_security_sast_codegraph_repopb_SetField_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.example.core.processor.bytedance.proto.CodeGraphStatement.internal_static_security_sast_codegraph_repopb_SetField_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.example.core.processor.bytedance.proto.SetField.class, org.example.core.processor.bytedance.proto.SetField.Builder.class);
  }

  private int fieldCase_ = 0;
  @SuppressWarnings("serial")
  private java.lang.Object field_;
  public enum FieldCase
      implements com.google.protobuf.Internal.EnumLite,
          com.google.protobuf.AbstractMessage.InternalOneOfEnum {
    NAME(3),
    VAR_ID(4),
    FIELD_NOT_SET(0);
    private final int value;
    private FieldCase(int value) {
      this.value = value;
    }
    /**
     * @param value The number of the enum to look for.
     * @return The enum associated with the given number.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static FieldCase valueOf(int value) {
      return forNumber(value);
    }

    public static FieldCase forNumber(int value) {
      switch (value) {
        case 3: return NAME;
        case 4: return VAR_ID;
        case 0: return FIELD_NOT_SET;
        default: return null;
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public FieldCase
  getFieldCase() {
    return FieldCase.forNumber(
        fieldCase_);
  }

  public static final int OBJECT_PTR_ID_FIELD_NUMBER = 1;
  private int objectPtrId_ = 0;
  /**
   * <code>int32 object_ptr_id = 1;</code>
   * @return The objectPtrId.
   */
  @java.lang.Override
  public int getObjectPtrId() {
    return objectPtrId_;
  }

  public static final int VALUE_ID_FIELD_NUMBER = 2;
  private int valueId_ = 0;
  /**
   * <code>int32 value_id = 2;</code>
   * @return The valueId.
   */
  @java.lang.Override
  public int getValueId() {
    return valueId_;
  }

  public static final int NAME_FIELD_NUMBER = 3;
  /**
   * <code>string name = 3;</code>
   * @return Whether the name field is set.
   */
  public boolean hasName() {
    return fieldCase_ == 3;
  }
  /**
   * <code>string name = 3;</code>
   * @return The name.
   */
  public java.lang.String getName() {
    java.lang.Object ref = "";
    if (fieldCase_ == 3) {
      ref = field_;
    }
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      if (fieldCase_ == 3) {
        field_ = s;
      }
      return s;
    }
  }
  /**
   * <code>string name = 3;</code>
   * @return The bytes for name.
   */
  public com.google.protobuf.ByteString
      getNameBytes() {
    java.lang.Object ref = "";
    if (fieldCase_ == 3) {
      ref = field_;
    }
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      if (fieldCase_ == 3) {
        field_ = b;
      }
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int VAR_ID_FIELD_NUMBER = 4;
  /**
   * <code>int32 var_id = 4;</code>
   * @return Whether the varId field is set.
   */
  @java.lang.Override
  public boolean hasVarId() {
    return fieldCase_ == 4;
  }
  /**
   * <code>int32 var_id = 4;</code>
   * @return The varId.
   */
  @java.lang.Override
  public int getVarId() {
    if (fieldCase_ == 4) {
      return (java.lang.Integer) field_;
    }
    return 0;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (objectPtrId_ != 0) {
      output.writeInt32(1, objectPtrId_);
    }
    if (valueId_ != 0) {
      output.writeInt32(2, valueId_);
    }
    if (fieldCase_ == 3) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, field_);
    }
    if (fieldCase_ == 4) {
      output.writeInt32(
          4, (int)((java.lang.Integer) field_));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (objectPtrId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, objectPtrId_);
    }
    if (valueId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, valueId_);
    }
    if (fieldCase_ == 3) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, field_);
    }
    if (fieldCase_ == 4) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(
            4, (int)((java.lang.Integer) field_));
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.example.core.processor.bytedance.proto.SetField)) {
      return super.equals(obj);
    }
    org.example.core.processor.bytedance.proto.SetField other = (org.example.core.processor.bytedance.proto.SetField) obj;

    if (getObjectPtrId()
        != other.getObjectPtrId()) return false;
    if (getValueId()
        != other.getValueId()) return false;
    if (!getFieldCase().equals(other.getFieldCase())) return false;
    switch (fieldCase_) {
      case 3:
        if (!getName()
            .equals(other.getName())) return false;
        break;
      case 4:
        if (getVarId()
            != other.getVarId()) return false;
        break;
      case 0:
      default:
    }
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + OBJECT_PTR_ID_FIELD_NUMBER;
    hash = (53 * hash) + getObjectPtrId();
    hash = (37 * hash) + VALUE_ID_FIELD_NUMBER;
    hash = (53 * hash) + getValueId();
    switch (fieldCase_) {
      case 3:
        hash = (37 * hash) + NAME_FIELD_NUMBER;
        hash = (53 * hash) + getName().hashCode();
        break;
      case 4:
        hash = (37 * hash) + VAR_ID_FIELD_NUMBER;
        hash = (53 * hash) + getVarId();
        break;
      case 0:
      default:
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.example.core.processor.bytedance.proto.SetField parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static org.example.core.processor.bytedance.proto.SetField parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static org.example.core.processor.bytedance.proto.SetField parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.example.core.processor.bytedance.proto.SetField parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.example.core.processor.bytedance.proto.SetField prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * SetField object-&gt;field = value
   * </pre>
   *
   * Protobuf type {@code security.sast.codegraph.repopb.SetField}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:security.sast.codegraph.repopb.SetField)
      org.example.core.processor.bytedance.proto.SetFieldOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.example.core.processor.bytedance.proto.CodeGraphStatement.internal_static_security_sast_codegraph_repopb_SetField_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.example.core.processor.bytedance.proto.CodeGraphStatement.internal_static_security_sast_codegraph_repopb_SetField_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.example.core.processor.bytedance.proto.SetField.class, org.example.core.processor.bytedance.proto.SetField.Builder.class);
    }

    // Construct using org.example.core.processor.bytedance.proto.SetField.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      objectPtrId_ = 0;
      valueId_ = 0;
      fieldCase_ = 0;
      field_ = null;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.example.core.processor.bytedance.proto.CodeGraphStatement.internal_static_security_sast_codegraph_repopb_SetField_descriptor;
    }

    @java.lang.Override
    public org.example.core.processor.bytedance.proto.SetField getDefaultInstanceForType() {
      return org.example.core.processor.bytedance.proto.SetField.getDefaultInstance();
    }

    @java.lang.Override
    public org.example.core.processor.bytedance.proto.SetField build() {
      org.example.core.processor.bytedance.proto.SetField result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.example.core.processor.bytedance.proto.SetField buildPartial() {
      org.example.core.processor.bytedance.proto.SetField result = new org.example.core.processor.bytedance.proto.SetField(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      buildPartialOneofs(result);
      onBuilt();
      return result;
    }

    private void buildPartial0(org.example.core.processor.bytedance.proto.SetField result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.objectPtrId_ = objectPtrId_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.valueId_ = valueId_;
      }
    }

    private void buildPartialOneofs(org.example.core.processor.bytedance.proto.SetField result) {
      result.fieldCase_ = fieldCase_;
      result.field_ = this.field_;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.example.core.processor.bytedance.proto.SetField) {
        return mergeFrom((org.example.core.processor.bytedance.proto.SetField)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.example.core.processor.bytedance.proto.SetField other) {
      if (other == org.example.core.processor.bytedance.proto.SetField.getDefaultInstance()) return this;
      if (other.getObjectPtrId() != 0) {
        setObjectPtrId(other.getObjectPtrId());
      }
      if (other.getValueId() != 0) {
        setValueId(other.getValueId());
      }
      switch (other.getFieldCase()) {
        case NAME: {
          fieldCase_ = 3;
          field_ = other.field_;
          onChanged();
          break;
        }
        case VAR_ID: {
          setVarId(other.getVarId());
          break;
        }
        case FIELD_NOT_SET: {
          break;
        }
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              objectPtrId_ = input.readInt32();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 16: {
              valueId_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();
              fieldCase_ = 3;
              field_ = s;
              break;
            } // case 26
            case 32: {
              field_ = input.readInt32();
              fieldCase_ = 4;
              break;
            } // case 32
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int fieldCase_ = 0;
    private java.lang.Object field_;
    public FieldCase
        getFieldCase() {
      return FieldCase.forNumber(
          fieldCase_);
    }

    public Builder clearField() {
      fieldCase_ = 0;
      field_ = null;
      onChanged();
      return this;
    }

    private int bitField0_;

    private int objectPtrId_ ;
    /**
     * <code>int32 object_ptr_id = 1;</code>
     * @return The objectPtrId.
     */
    @java.lang.Override
    public int getObjectPtrId() {
      return objectPtrId_;
    }
    /**
     * <code>int32 object_ptr_id = 1;</code>
     * @param value The objectPtrId to set.
     * @return This builder for chaining.
     */
    public Builder setObjectPtrId(int value) {

      objectPtrId_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int32 object_ptr_id = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearObjectPtrId() {
      bitField0_ = (bitField0_ & ~0x00000001);
      objectPtrId_ = 0;
      onChanged();
      return this;
    }

    private int valueId_ ;
    /**
     * <code>int32 value_id = 2;</code>
     * @return The valueId.
     */
    @java.lang.Override
    public int getValueId() {
      return valueId_;
    }
    /**
     * <code>int32 value_id = 2;</code>
     * @param value The valueId to set.
     * @return This builder for chaining.
     */
    public Builder setValueId(int value) {

      valueId_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 value_id = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearValueId() {
      bitField0_ = (bitField0_ & ~0x00000002);
      valueId_ = 0;
      onChanged();
      return this;
    }

    /**
     * <code>string name = 3;</code>
     * @return Whether the name field is set.
     */
    @java.lang.Override
    public boolean hasName() {
      return fieldCase_ == 3;
    }
    /**
     * <code>string name = 3;</code>
     * @return The name.
     */
    @java.lang.Override
    public java.lang.String getName() {
      java.lang.Object ref = "";
      if (fieldCase_ == 3) {
        ref = field_;
      }
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (fieldCase_ == 3) {
          field_ = s;
        }
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string name = 3;</code>
     * @return The bytes for name.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = "";
      if (fieldCase_ == 3) {
        ref = field_;
      }
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        if (fieldCase_ == 3) {
          field_ = b;
        }
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string name = 3;</code>
     * @param value The name to set.
     * @return This builder for chaining.
     */
    public Builder setName(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      fieldCase_ = 3;
      field_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string name = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearName() {
      if (fieldCase_ == 3) {
        fieldCase_ = 0;
        field_ = null;
        onChanged();
      }
      return this;
    }
    /**
     * <code>string name = 3;</code>
     * @param value The bytes for name to set.
     * @return This builder for chaining.
     */
    public Builder setNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      fieldCase_ = 3;
      field_ = value;
      onChanged();
      return this;
    }

    /**
     * <code>int32 var_id = 4;</code>
     * @return Whether the varId field is set.
     */
    public boolean hasVarId() {
      return fieldCase_ == 4;
    }
    /**
     * <code>int32 var_id = 4;</code>
     * @return The varId.
     */
    public int getVarId() {
      if (fieldCase_ == 4) {
        return (java.lang.Integer) field_;
      }
      return 0;
    }
    /**
     * <code>int32 var_id = 4;</code>
     * @param value The varId to set.
     * @return This builder for chaining.
     */
    public Builder setVarId(int value) {

      fieldCase_ = 4;
      field_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 var_id = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearVarId() {
      if (fieldCase_ == 4) {
        fieldCase_ = 0;
        field_ = null;
        onChanged();
      }
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:security.sast.codegraph.repopb.SetField)
  }

  // @@protoc_insertion_point(class_scope:security.sast.codegraph.repopb.SetField)
  private static final org.example.core.processor.bytedance.proto.SetField DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.example.core.processor.bytedance.proto.SetField();
  }

  public static org.example.core.processor.bytedance.proto.SetField getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SetField>
      PARSER = new com.google.protobuf.AbstractParser<SetField>() {
    @java.lang.Override
    public SetField parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<SetField> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SetField> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.example.core.processor.bytedance.proto.SetField getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
