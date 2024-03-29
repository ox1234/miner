// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: stmt.proto

package org.example.core.hook.bytedance.proto;

/**
 * <pre>
 * Location 代码位置
 * </pre>
 *
 * Protobuf type {@code security.sast.codegraph.repopb.Pos}
 */
public final class Pos extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:security.sast.codegraph.repopb.Pos)
    PosOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Pos.newBuilder() to construct.
  private Pos(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Pos() {
    file_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Pos();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_Pos_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_Pos_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            Pos.class, Pos.Builder.class);
  }

  public static final int FILE_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private volatile java.lang.Object file_ = "";
  /**
   * <pre>
   * 文件名
   * </pre>
   *
   * <code>string file = 1;</code>
   * @return The file.
   */
  @java.lang.Override
  public java.lang.String getFile() {
    java.lang.Object ref = file_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      file_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * 文件名
   * </pre>
   *
   * <code>string file = 1;</code>
   * @return The bytes for file.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getFileBytes() {
    java.lang.Object ref = file_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      file_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int LINE_FIELD_NUMBER = 2;
  private int line_ = 0;
  /**
   * <pre>
   * 行号
   * </pre>
   *
   * <code>int32 line = 2;</code>
   * @return The line.
   */
  @java.lang.Override
  public int getLine() {
    return line_;
  }

  public static final int COLUMN_FIELD_NUMBER = 3;
  private int column_ = 0;
  /**
   * <pre>
   * 列号
   * </pre>
   *
   * <code>int32 column = 3;</code>
   * @return The column.
   */
  @java.lang.Override
  public int getColumn() {
    return column_;
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(file_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, file_);
    }
    if (line_ != 0) {
      output.writeInt32(2, line_);
    }
    if (column_ != 0) {
      output.writeInt32(3, column_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(file_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, file_);
    }
    if (line_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, line_);
    }
    if (column_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, column_);
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
    if (!(obj instanceof Pos)) {
      return super.equals(obj);
    }
    Pos other = (Pos) obj;

    if (!getFile()
        .equals(other.getFile())) return false;
    if (getLine()
        != other.getLine()) return false;
    if (getColumn()
        != other.getColumn()) return false;
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
    hash = (37 * hash) + FILE_FIELD_NUMBER;
    hash = (53 * hash) + getFile().hashCode();
    hash = (37 * hash) + LINE_FIELD_NUMBER;
    hash = (53 * hash) + getLine();
    hash = (37 * hash) + COLUMN_FIELD_NUMBER;
    hash = (53 * hash) + getColumn();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static Pos parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static Pos parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static Pos parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static Pos parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static Pos parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static Pos parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static Pos parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static Pos parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static Pos parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static Pos parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static Pos parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static Pos parseFrom(
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
  public static Builder newBuilder(Pos prototype) {
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
   * Location 代码位置
   * </pre>
   *
   * Protobuf type {@code security.sast.codegraph.repopb.Pos}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:security.sast.codegraph.repopb.Pos)
          PosOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_Pos_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_Pos_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              Pos.class, Pos.Builder.class);
    }

    // Construct using org.example.core.processor.bytedance.proto.Pos.newBuilder()
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
      file_ = "";
      line_ = 0;
      column_ = 0;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return CodeGraphStatement.internal_static_security_sast_codegraph_repopb_Pos_descriptor;
    }

    @java.lang.Override
    public Pos getDefaultInstanceForType() {
      return Pos.getDefaultInstance();
    }

    @java.lang.Override
    public Pos build() {
      Pos result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public Pos buildPartial() {
      Pos result = new Pos(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(Pos result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.file_ = file_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.line_ = line_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.column_ = column_;
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
      if (other instanceof Pos) {
        return mergeFrom((Pos)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(Pos other) {
      if (other == Pos.getDefaultInstance()) return this;
      if (!other.getFile().isEmpty()) {
        file_ = other.file_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (other.getLine() != 0) {
        setLine(other.getLine());
      }
      if (other.getColumn() != 0) {
        setColumn(other.getColumn());
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
            case 10: {
              file_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 16: {
              line_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
            case 24: {
              column_ = input.readInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 24
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

    private java.lang.Object file_ = "";
    /**
     * <pre>
     * 文件名
     * </pre>
     *
     * <code>string file = 1;</code>
     * @return The file.
     */
    public java.lang.String getFile() {
      java.lang.Object ref = file_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        file_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * 文件名
     * </pre>
     *
     * <code>string file = 1;</code>
     * @return The bytes for file.
     */
    public com.google.protobuf.ByteString
        getFileBytes() {
      java.lang.Object ref = file_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        file_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * 文件名
     * </pre>
     *
     * <code>string file = 1;</code>
     * @param value The file to set.
     * @return This builder for chaining.
     */
    public Builder setFile(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      file_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 文件名
     * </pre>
     *
     * <code>string file = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearFile() {
      file_ = getDefaultInstance().getFile();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 文件名
     * </pre>
     *
     * <code>string file = 1;</code>
     * @param value The bytes for file to set.
     * @return This builder for chaining.
     */
    public Builder setFileBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      file_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private int line_ ;
    /**
     * <pre>
     * 行号
     * </pre>
     *
     * <code>int32 line = 2;</code>
     * @return The line.
     */
    @java.lang.Override
    public int getLine() {
      return line_;
    }
    /**
     * <pre>
     * 行号
     * </pre>
     *
     * <code>int32 line = 2;</code>
     * @param value The line to set.
     * @return This builder for chaining.
     */
    public Builder setLine(int value) {

      line_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 行号
     * </pre>
     *
     * <code>int32 line = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearLine() {
      bitField0_ = (bitField0_ & ~0x00000002);
      line_ = 0;
      onChanged();
      return this;
    }

    private int column_ ;
    /**
     * <pre>
     * 列号
     * </pre>
     *
     * <code>int32 column = 3;</code>
     * @return The column.
     */
    @java.lang.Override
    public int getColumn() {
      return column_;
    }
    /**
     * <pre>
     * 列号
     * </pre>
     *
     * <code>int32 column = 3;</code>
     * @param value The column to set.
     * @return This builder for chaining.
     */
    public Builder setColumn(int value) {

      column_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 列号
     * </pre>
     *
     * <code>int32 column = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearColumn() {
      bitField0_ = (bitField0_ & ~0x00000004);
      column_ = 0;
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


    // @@protoc_insertion_point(builder_scope:security.sast.codegraph.repopb.Pos)
  }

  // @@protoc_insertion_point(class_scope:security.sast.codegraph.repopb.Pos)
  private static final Pos DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new Pos();
  }

  public static Pos getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Pos>
      PARSER = new com.google.protobuf.AbstractParser<Pos>() {
    @java.lang.Override
    public Pos parsePartialFrom(
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

  public static com.google.protobuf.Parser<Pos> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Pos> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public Pos getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

