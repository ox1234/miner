// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: stmt.proto

package org.example.core.processor.bytedance.proto;

public interface StatementOrBuilder extends
    // @@protoc_insertion_point(interface_extends:security.sast.codegraph.repopb.Statement)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.security.sast.codegraph.repopb.CommonFlow common_flow = 1;</code>
   * @return Whether the commonFlow field is set.
   */
  boolean hasCommonFlow();
  /**
   * <code>.security.sast.codegraph.repopb.CommonFlow common_flow = 1;</code>
   * @return The commonFlow.
   */
  org.example.core.processor.bytedance.proto.CommonFlow getCommonFlow();
  /**
   * <code>.security.sast.codegraph.repopb.CommonFlow common_flow = 1;</code>
   */
  org.example.core.processor.bytedance.proto.CommonFlowOrBuilder getCommonFlowOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.Call call = 2;</code>
   * @return Whether the call field is set.
   */
  boolean hasCall();
  /**
   * <code>.security.sast.codegraph.repopb.Call call = 2;</code>
   * @return The call.
   */
  org.example.core.processor.bytedance.proto.Call getCall();
  /**
   * <code>.security.sast.codegraph.repopb.Call call = 2;</code>
   */
  org.example.core.processor.bytedance.proto.CallOrBuilder getCallOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.SetField set_field = 3;</code>
   * @return Whether the setField field is set.
   */
  boolean hasSetField();
  /**
   * <code>.security.sast.codegraph.repopb.SetField set_field = 3;</code>
   * @return The setField.
   */
  org.example.core.processor.bytedance.proto.SetField getSetField();
  /**
   * <code>.security.sast.codegraph.repopb.SetField set_field = 3;</code>
   */
  org.example.core.processor.bytedance.proto.SetFieldOrBuilder getSetFieldOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.StorePtr store_ptr = 4;</code>
   * @return Whether the storePtr field is set.
   */
  boolean hasStorePtr();
  /**
   * <code>.security.sast.codegraph.repopb.StorePtr store_ptr = 4;</code>
   * @return The storePtr.
   */
  org.example.core.processor.bytedance.proto.StorePtr getStorePtr();
  /**
   * <code>.security.sast.codegraph.repopb.StorePtr store_ptr = 4;</code>
   */
  org.example.core.processor.bytedance.proto.StorePtrOrBuilder getStorePtrOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.GetFieldAddr get_field_addr = 5;</code>
   * @return Whether the getFieldAddr field is set.
   */
  boolean hasGetFieldAddr();
  /**
   * <code>.security.sast.codegraph.repopb.GetFieldAddr get_field_addr = 5;</code>
   * @return The getFieldAddr.
   */
  org.example.core.processor.bytedance.proto.GetFieldAddr getGetFieldAddr();
  /**
   * <code>.security.sast.codegraph.repopb.GetFieldAddr get_field_addr = 5;</code>
   */
  org.example.core.processor.bytedance.proto.GetFieldAddrOrBuilder getGetFieldAddrOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.GetField get_field = 6;</code>
   * @return Whether the getField field is set.
   */
  boolean hasGetField();
  /**
   * <code>.security.sast.codegraph.repopb.GetField get_field = 6;</code>
   * @return The getField.
   */
  org.example.core.processor.bytedance.proto.GetField getGetField();
  /**
   * <code>.security.sast.codegraph.repopb.GetField get_field = 6;</code>
   */
  org.example.core.processor.bytedance.proto.GetFieldOrBuilder getGetFieldOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.LoadPtr load_ptr = 7;</code>
   * @return Whether the loadPtr field is set.
   */
  boolean hasLoadPtr();
  /**
   * <code>.security.sast.codegraph.repopb.LoadPtr load_ptr = 7;</code>
   * @return The loadPtr.
   */
  org.example.core.processor.bytedance.proto.LoadPtr getLoadPtr();
  /**
   * <code>.security.sast.codegraph.repopb.LoadPtr load_ptr = 7;</code>
   */
  org.example.core.processor.bytedance.proto.LoadPtrOrBuilder getLoadPtrOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.MakeClosure make_closure = 8;</code>
   * @return Whether the makeClosure field is set.
   */
  boolean hasMakeClosure();
  /**
   * <code>.security.sast.codegraph.repopb.MakeClosure make_closure = 8;</code>
   * @return The makeClosure.
   */
  org.example.core.processor.bytedance.proto.MakeClosure getMakeClosure();
  /**
   * <code>.security.sast.codegraph.repopb.MakeClosure make_closure = 8;</code>
   */
  org.example.core.processor.bytedance.proto.MakeClosureOrBuilder getMakeClosureOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.Phi phi = 9;</code>
   * @return Whether the phi field is set.
   */
  boolean hasPhi();
  /**
   * <code>.security.sast.codegraph.repopb.Phi phi = 9;</code>
   * @return The phi.
   */
  org.example.core.processor.bytedance.proto.Phi getPhi();
  /**
   * <code>.security.sast.codegraph.repopb.Phi phi = 9;</code>
   */
  org.example.core.processor.bytedance.proto.PhiOrBuilder getPhiOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.Operation operation = 10;</code>
   * @return Whether the operation field is set.
   */
  boolean hasOperation();
  /**
   * <code>.security.sast.codegraph.repopb.Operation operation = 10;</code>
   * @return The operation.
   */
  org.example.core.processor.bytedance.proto.Operation getOperation();
  /**
   * <code>.security.sast.codegraph.repopb.Operation operation = 10;</code>
   */
  org.example.core.processor.bytedance.proto.OperationOrBuilder getOperationOrBuilder();

  /**
   * <code>.security.sast.codegraph.repopb.Pos pos = 101;</code>
   * @return Whether the pos field is set.
   */
  boolean hasPos();
  /**
   * <code>.security.sast.codegraph.repopb.Pos pos = 101;</code>
   * @return The pos.
   */
  org.example.core.processor.bytedance.proto.Pos getPos();
  /**
   * <code>.security.sast.codegraph.repopb.Pos pos = 101;</code>
   */
  org.example.core.processor.bytedance.proto.PosOrBuilder getPosOrBuilder();

  org.example.core.processor.bytedance.proto.Statement.KindCase getKindCase();
}
