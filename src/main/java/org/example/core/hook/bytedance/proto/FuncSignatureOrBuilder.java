// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: repo.proto

package org.example.core.hook.bytedance.proto;

public interface FuncSignatureOrBuilder extends
    // @@protoc_insertion_point(interface_extends:security.sast.codegraph.repopb.FuncSignature)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * if the function is a method
   * </pre>
   *
   * <code>string class_type = 1;</code>
   * @return The classType.
   */
  java.lang.String getClassType();
  /**
   * <pre>
   * if the function is a method
   * </pre>
   *
   * <code>string class_type = 1;</code>
   * @return The bytes for classType.
   */
  com.google.protobuf.ByteString
      getClassTypeBytes();

  /**
   * <code>repeated string arg_types = 2;</code>
   * @return A list containing the argTypes.
   */
  java.util.List<java.lang.String>
      getArgTypesList();
  /**
   * <code>repeated string arg_types = 2;</code>
   * @return The count of argTypes.
   */
  int getArgTypesCount();
  /**
   * <code>repeated string arg_types = 2;</code>
   * @param index The index of the element to return.
   * @return The argTypes at the given index.
   */
  java.lang.String getArgTypes(int index);
  /**
   * <code>repeated string arg_types = 2;</code>
   * @param index The index of the value to return.
   * @return The bytes of the argTypes at the given index.
   */
  com.google.protobuf.ByteString
      getArgTypesBytes(int index);

  /**
   * <code>repeated string ret_types = 3;</code>
   * @return A list containing the retTypes.
   */
  java.util.List<java.lang.String>
      getRetTypesList();
  /**
   * <code>repeated string ret_types = 3;</code>
   * @return The count of retTypes.
   */
  int getRetTypesCount();
  /**
   * <code>repeated string ret_types = 3;</code>
   * @param index The index of the element to return.
   * @return The retTypes at the given index.
   */
  java.lang.String getRetTypes(int index);
  /**
   * <code>repeated string ret_types = 3;</code>
   * @param index The index of the value to return.
   * @return The bytes of the retTypes at the given index.
   */
  com.google.protobuf.ByteString
      getRetTypesBytes(int index);
}
