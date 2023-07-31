// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: repo.proto

package org.example.core.processor.bytedance.proto;

/**
 * Protobuf enum {@code security.sast.codegraph.repopb.Language}
 */
public enum Language
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>GOLANG = 0;</code>
   */
  GOLANG(0),
  /**
   * <code>JAVA = 1;</code>
   */
  JAVA(1),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>GOLANG = 0;</code>
   */
  public static final int GOLANG_VALUE = 0;
  /**
   * <code>JAVA = 1;</code>
   */
  public static final int JAVA_VALUE = 1;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static Language valueOf(int value) {
    return forNumber(value);
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   */
  public static Language forNumber(int value) {
    switch (value) {
      case 0: return GOLANG;
      case 1: return JAVA;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<Language>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      Language> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<Language>() {
          public Language findValueByNumber(int number) {
            return Language.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalStateException(
          "Can't get the descriptor of an unrecognized enum value.");
    }
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return org.example.core.processor.bytedance.proto.CodeGraphRepo.getDescriptor().getEnumTypes().get(0);
  }

  private static final Language[] VALUES = values();

  public static Language valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private Language(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:security.sast.codegraph.repopb.Language)
}
