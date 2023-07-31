// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: stmt.proto

package org.example.core.hook.bytedance.proto;

/**
 * <pre>
 * CommonFlow target &lt;= [sources]
 * </pre>
 *
 * Protobuf type {@code security.sast.codegraph.repopb.CommonFlow}
 */
public final class CommonFlow extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:security.sast.codegraph.repopb.CommonFlow)
    CommonFlowOrBuilder {
private static final long serialVersionUID = 0L;
  // Use CommonFlow.newBuilder() to construct.
  private CommonFlow(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private CommonFlow() {
    sourceIds_ = emptyIntList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new CommonFlow();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_CommonFlow_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_CommonFlow_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            CommonFlow.class, CommonFlow.Builder.class);
  }

  public static final int TARGET_ID_FIELD_NUMBER = 1;
  private int targetId_ = 0;
  /**
   * <code>int32 target_id = 1;</code>
   * @return The targetId.
   */
  @java.lang.Override
  public int getTargetId() {
    return targetId_;
  }

  public static final int SOURCE_IDS_FIELD_NUMBER = 2;
  @SuppressWarnings("serial")
  private com.google.protobuf.Internal.IntList sourceIds_;
  /**
   * <code>repeated int32 source_ids = 2;</code>
   * @return A list containing the sourceIds.
   */
  @java.lang.Override
  public java.util.List<java.lang.Integer>
      getSourceIdsList() {
    return sourceIds_;
  }
  /**
   * <code>repeated int32 source_ids = 2;</code>
   * @return The count of sourceIds.
   */
  public int getSourceIdsCount() {
    return sourceIds_.size();
  }
  /**
   * <code>repeated int32 source_ids = 2;</code>
   * @param index The index of the element to return.
   * @return The sourceIds at the given index.
   */
  public int getSourceIds(int index) {
    return sourceIds_.getInt(index);
  }
  private int sourceIdsMemoizedSerializedSize = -1;

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
    getSerializedSize();
    if (targetId_ != 0) {
      output.writeInt32(1, targetId_);
    }
    if (getSourceIdsList().size() > 0) {
      output.writeUInt32NoTag(18);
      output.writeUInt32NoTag(sourceIdsMemoizedSerializedSize);
    }
    for (int i = 0; i < sourceIds_.size(); i++) {
      output.writeInt32NoTag(sourceIds_.getInt(i));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (targetId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, targetId_);
    }
    {
      int dataSize = 0;
      for (int i = 0; i < sourceIds_.size(); i++) {
        dataSize += com.google.protobuf.CodedOutputStream
          .computeInt32SizeNoTag(sourceIds_.getInt(i));
      }
      size += dataSize;
      if (!getSourceIdsList().isEmpty()) {
        size += 1;
        size += com.google.protobuf.CodedOutputStream
            .computeInt32SizeNoTag(dataSize);
      }
      sourceIdsMemoizedSerializedSize = dataSize;
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
    if (!(obj instanceof CommonFlow)) {
      return super.equals(obj);
    }
    CommonFlow other = (CommonFlow) obj;

    if (getTargetId()
        != other.getTargetId()) return false;
    if (!getSourceIdsList()
        .equals(other.getSourceIdsList())) return false;
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
    hash = (37 * hash) + TARGET_ID_FIELD_NUMBER;
    hash = (53 * hash) + getTargetId();
    if (getSourceIdsCount() > 0) {
      hash = (37 * hash) + SOURCE_IDS_FIELD_NUMBER;
      hash = (53 * hash) + getSourceIdsList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static CommonFlow parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static CommonFlow parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static CommonFlow parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static CommonFlow parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static CommonFlow parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static CommonFlow parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static CommonFlow parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static CommonFlow parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static CommonFlow parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static CommonFlow parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static CommonFlow parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static CommonFlow parseFrom(
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
  public static Builder newBuilder(CommonFlow prototype) {
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
   * CommonFlow target &lt;= [sources]
   * </pre>
   *
   * Protobuf type {@code security.sast.codegraph.repopb.CommonFlow}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:security.sast.codegraph.repopb.CommonFlow)
          CommonFlowOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_CommonFlow_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_CommonFlow_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              CommonFlow.class, CommonFlow.Builder.class);
    }

    // Construct using org.example.core.processor.bytedance.proto.CommonFlow.newBuilder()
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
      targetId_ = 0;
      sourceIds_ = emptyIntList();
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_CommonFlow_descriptor;
    }

    @java.lang.Override
    public CommonFlow getDefaultInstanceForType() {
      return CommonFlow.getDefaultInstance();
    }

    @java.lang.Override
    public CommonFlow build() {
      CommonFlow result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public CommonFlow buildPartial() {
      CommonFlow result = new CommonFlow(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(CommonFlow result) {
      if (((bitField0_ & 0x00000002) != 0)) {
        sourceIds_.makeImmutable();
        bitField0_ = (bitField0_ & ~0x00000002);
      }
      result.sourceIds_ = sourceIds_;
    }

    private void buildPartial0(CommonFlow result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.targetId_ = targetId_;
      }
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
      if (other instanceof CommonFlow) {
        return mergeFrom((CommonFlow)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(CommonFlow other) {
      if (other == CommonFlow.getDefaultInstance()) return this;
      if (other.getTargetId() != 0) {
        setTargetId(other.getTargetId());
      }
      if (!other.sourceIds_.isEmpty()) {
        if (sourceIds_.isEmpty()) {
          sourceIds_ = other.sourceIds_;
          bitField0_ = (bitField0_ & ~0x00000002);
        } else {
          ensureSourceIdsIsMutable();
          sourceIds_.addAll(other.sourceIds_);
        }
        onChanged();
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
              targetId_ = input.readInt32();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 16: {
              int v = input.readInt32();
              ensureSourceIdsIsMutable();
              sourceIds_.addInt(v);
              break;
            } // case 16
            case 18: {
              int length = input.readRawVarint32();
              int limit = input.pushLimit(length);
              ensureSourceIdsIsMutable();
              while (input.getBytesUntilLimit() > 0) {
                sourceIds_.addInt(input.readInt32());
              }
              input.popLimit(limit);
              break;
            } // case 18
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
    private int bitField0_;

    private int targetId_ ;
    /**
     * <code>int32 target_id = 1;</code>
     * @return The targetId.
     */
    @java.lang.Override
    public int getTargetId() {
      return targetId_;
    }
    /**
     * <code>int32 target_id = 1;</code>
     * @param value The targetId to set.
     * @return This builder for chaining.
     */
    public Builder setTargetId(int value) {

      targetId_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int32 target_id = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearTargetId() {
      bitField0_ = (bitField0_ & ~0x00000001);
      targetId_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.Internal.IntList sourceIds_ = emptyIntList();
    private void ensureSourceIdsIsMutable() {
      if (!((bitField0_ & 0x00000002) != 0)) {
        sourceIds_ = mutableCopy(sourceIds_);
        bitField0_ |= 0x00000002;
      }
    }
    /**
     * <code>repeated int32 source_ids = 2;</code>
     * @return A list containing the sourceIds.
     */
    public java.util.List<java.lang.Integer>
        getSourceIdsList() {
      return ((bitField0_ & 0x00000002) != 0) ?
               java.util.Collections.unmodifiableList(sourceIds_) : sourceIds_;
    }
    /**
     * <code>repeated int32 source_ids = 2;</code>
     * @return The count of sourceIds.
     */
    public int getSourceIdsCount() {
      return sourceIds_.size();
    }
    /**
     * <code>repeated int32 source_ids = 2;</code>
     * @param index The index of the element to return.
     * @return The sourceIds at the given index.
     */
    public int getSourceIds(int index) {
      return sourceIds_.getInt(index);
    }
    /**
     * <code>repeated int32 source_ids = 2;</code>
     * @param index The index to set the value at.
     * @param value The sourceIds to set.
     * @return This builder for chaining.
     */
    public Builder setSourceIds(
        int index, int value) {

      ensureSourceIdsIsMutable();
      sourceIds_.setInt(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 source_ids = 2;</code>
     * @param value The sourceIds to add.
     * @return This builder for chaining.
     */
    public Builder addSourceIds(int value) {

      ensureSourceIdsIsMutable();
      sourceIds_.addInt(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 source_ids = 2;</code>
     * @param values The sourceIds to add.
     * @return This builder for chaining.
     */
    public Builder addAllSourceIds(
        java.lang.Iterable<? extends java.lang.Integer> values) {
      ensureSourceIdsIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, sourceIds_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 source_ids = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearSourceIds() {
      sourceIds_ = emptyIntList();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
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


    // @@protoc_insertion_point(builder_scope:security.sast.codegraph.repopb.CommonFlow)
  }

  // @@protoc_insertion_point(class_scope:security.sast.codegraph.repopb.CommonFlow)
  private static final CommonFlow DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new CommonFlow();
  }

  public static CommonFlow getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CommonFlow>
      PARSER = new com.google.protobuf.AbstractParser<CommonFlow>() {
    @java.lang.Override
    public CommonFlow parsePartialFrom(
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

  public static com.google.protobuf.Parser<CommonFlow> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CommonFlow> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public CommonFlow getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
