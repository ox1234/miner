// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: repo.proto

package org.example.core.hook.bytedance.proto;

/**
 * Protobuf type {@code security.sast.codegraph.repopb.GlobalNode}
 */
public final class GlobalNode extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:security.sast.codegraph.repopb.GlobalNode)
    GlobalNodeOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GlobalNode.newBuilder() to construct.
  private GlobalNode(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GlobalNode() {
    globalId_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new GlobalNode();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return CodeGraphRepo.internal_static_security_sast_codegraph_repopb_GlobalNode_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return CodeGraphRepo.internal_static_security_sast_codegraph_repopb_GlobalNode_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            GlobalNode.class, GlobalNode.Builder.class);
  }

  public static final int GLOBAL_ID_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private volatile java.lang.Object globalId_ = "";
  /**
   * <code>string global_id = 1;</code>
   * @return The globalId.
   */
  @java.lang.Override
  public java.lang.String getGlobalId() {
    java.lang.Object ref = globalId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      globalId_ = s;
      return s;
    }
  }
  /**
   * <code>string global_id = 1;</code>
   * @return The bytes for globalId.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getGlobalIdBytes() {
    java.lang.Object ref = globalId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      globalId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int NODE_FIELD_NUMBER = 2;
  private Node node_;
  /**
   * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
   * @return Whether the node field is set.
   */
  @java.lang.Override
  public boolean hasNode() {
    return node_ != null;
  }
  /**
   * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
   * @return The node.
   */
  @java.lang.Override
  public Node getNode() {
    return node_ == null ? Node.getDefaultInstance() : node_;
  }
  /**
   * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
   */
  @java.lang.Override
  public NodeOrBuilder getNodeOrBuilder() {
    return node_ == null ? Node.getDefaultInstance() : node_;
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
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(globalId_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, globalId_);
    }
    if (node_ != null) {
      output.writeMessage(2, getNode());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(globalId_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, globalId_);
    }
    if (node_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getNode());
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
    if (!(obj instanceof GlobalNode)) {
      return super.equals(obj);
    }
    GlobalNode other = (GlobalNode) obj;

    if (!getGlobalId()
        .equals(other.getGlobalId())) return false;
    if (hasNode() != other.hasNode()) return false;
    if (hasNode()) {
      if (!getNode()
          .equals(other.getNode())) return false;
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
    hash = (37 * hash) + GLOBAL_ID_FIELD_NUMBER;
    hash = (53 * hash) + getGlobalId().hashCode();
    if (hasNode()) {
      hash = (37 * hash) + NODE_FIELD_NUMBER;
      hash = (53 * hash) + getNode().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static GlobalNode parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static GlobalNode parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static GlobalNode parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static GlobalNode parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static GlobalNode parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static GlobalNode parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static GlobalNode parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static GlobalNode parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static GlobalNode parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static GlobalNode parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static GlobalNode parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static GlobalNode parseFrom(
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
  public static Builder newBuilder(GlobalNode prototype) {
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
   * Protobuf type {@code security.sast.codegraph.repopb.GlobalNode}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:security.sast.codegraph.repopb.GlobalNode)
          GlobalNodeOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return CodeGraphRepo.internal_static_security_sast_codegraph_repopb_GlobalNode_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return CodeGraphRepo.internal_static_security_sast_codegraph_repopb_GlobalNode_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              GlobalNode.class, GlobalNode.Builder.class);
    }

    // Construct using org.example.core.processor.bytedance.proto.GlobalNode.newBuilder()
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
      globalId_ = "";
      node_ = null;
      if (nodeBuilder_ != null) {
        nodeBuilder_.dispose();
        nodeBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return CodeGraphRepo.internal_static_security_sast_codegraph_repopb_GlobalNode_descriptor;
    }

    @java.lang.Override
    public GlobalNode getDefaultInstanceForType() {
      return GlobalNode.getDefaultInstance();
    }

    @java.lang.Override
    public GlobalNode build() {
      GlobalNode result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public GlobalNode buildPartial() {
      GlobalNode result = new GlobalNode(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(GlobalNode result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.globalId_ = globalId_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.node_ = nodeBuilder_ == null
            ? node_
            : nodeBuilder_.build();
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
      if (other instanceof GlobalNode) {
        return mergeFrom((GlobalNode)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(GlobalNode other) {
      if (other == GlobalNode.getDefaultInstance()) return this;
      if (!other.getGlobalId().isEmpty()) {
        globalId_ = other.globalId_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (other.hasNode()) {
        mergeNode(other.getNode());
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
              globalId_ = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              input.readMessage(
                  getNodeFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000002;
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

    private java.lang.Object globalId_ = "";
    /**
     * <code>string global_id = 1;</code>
     * @return The globalId.
     */
    public java.lang.String getGlobalId() {
      java.lang.Object ref = globalId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        globalId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string global_id = 1;</code>
     * @return The bytes for globalId.
     */
    public com.google.protobuf.ByteString
        getGlobalIdBytes() {
      java.lang.Object ref = globalId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        globalId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string global_id = 1;</code>
     * @param value The globalId to set.
     * @return This builder for chaining.
     */
    public Builder setGlobalId(
        java.lang.String value) {
      if (value == null) { throw new NullPointerException(); }
      globalId_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>string global_id = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearGlobalId() {
      globalId_ = getDefaultInstance().getGlobalId();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <code>string global_id = 1;</code>
     * @param value The bytes for globalId to set.
     * @return This builder for chaining.
     */
    public Builder setGlobalIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      checkByteStringIsUtf8(value);
      globalId_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private Node node_;
    private com.google.protobuf.SingleFieldBuilderV3<
            Node, Node.Builder, NodeOrBuilder> nodeBuilder_;
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     * @return Whether the node field is set.
     */
    public boolean hasNode() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     * @return The node.
     */
    public Node getNode() {
      if (nodeBuilder_ == null) {
        return node_ == null ? Node.getDefaultInstance() : node_;
      } else {
        return nodeBuilder_.getMessage();
      }
    }
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     */
    public Builder setNode(Node value) {
      if (nodeBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        node_ = value;
      } else {
        nodeBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     */
    public Builder setNode(
        Node.Builder builderForValue) {
      if (nodeBuilder_ == null) {
        node_ = builderForValue.build();
      } else {
        nodeBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     */
    public Builder mergeNode(Node value) {
      if (nodeBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
          node_ != null &&
          node_ != Node.getDefaultInstance()) {
          getNodeBuilder().mergeFrom(value);
        } else {
          node_ = value;
        }
      } else {
        nodeBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     */
    public Builder clearNode() {
      bitField0_ = (bitField0_ & ~0x00000002);
      node_ = null;
      if (nodeBuilder_ != null) {
        nodeBuilder_.dispose();
        nodeBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     */
    public Node.Builder getNodeBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getNodeFieldBuilder().getBuilder();
    }
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     */
    public NodeOrBuilder getNodeOrBuilder() {
      if (nodeBuilder_ != null) {
        return nodeBuilder_.getMessageOrBuilder();
      } else {
        return node_ == null ?
            Node.getDefaultInstance() : node_;
      }
    }
    /**
     * <code>.security.sast.codegraph.repopb.Node node = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            Node, Node.Builder, NodeOrBuilder>
        getNodeFieldBuilder() {
      if (nodeBuilder_ == null) {
        nodeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
                Node, Node.Builder, NodeOrBuilder>(
                getNode(),
                getParentForChildren(),
                isClean());
        node_ = null;
      }
      return nodeBuilder_;
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


    // @@protoc_insertion_point(builder_scope:security.sast.codegraph.repopb.GlobalNode)
  }

  // @@protoc_insertion_point(class_scope:security.sast.codegraph.repopb.GlobalNode)
  private static final GlobalNode DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new GlobalNode();
  }

  public static GlobalNode getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GlobalNode>
      PARSER = new com.google.protobuf.AbstractParser<GlobalNode>() {
    @java.lang.Override
    public GlobalNode parsePartialFrom(
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

  public static com.google.protobuf.Parser<GlobalNode> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GlobalNode> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public GlobalNode getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

