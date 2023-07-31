package org.example.core.hook.bytedance;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.SiteLevelSite;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.hook.bytedance.proto.*;
import org.example.util.TagUtil;
import soot.SootClass;
import soot.SootField;
import soot.Unit;

import java.util.Map;

public class CodeGraphConverter {
    public static org.example.core.hook.bytedance.proto.Node convertToCodeGraphNode(Node node) {
        org.example.core.hook.bytedance.proto.Node.Builder builder = org.example.core.hook.bytedance.proto.Node.newBuilder();
        if (node instanceof StaticField) {
            builder.setType(((StaticField) node).getType().toString());
            builder.setName(((StaticField) node).getFieldRef().getName());
        } else if (node instanceof LocalVariable) {
            builder.setType(((LocalVariable) node).getType().toString());
            builder.setName(((LocalVariable) node).getName());
        }
        return builder.build();
    }

    public static Function.Variable convertToVariable(Node node) {
        Function.Variable.Builder builder = Function.Variable.newBuilder();
        if (node instanceof LocalVariable) {
            builder.setLocalNode(convertToCodeGraphNode(node));
        } else if (node instanceof StaticField) {
            builder.setGlobalRef(GlobalReference.newBuilder().setGlobalId(node.getID()).build());
        }
        return builder.build();
    }

    public static GlobalNode convertToGlobalNode(SootField sootField) {
        GlobalNode.Builder builder = GlobalNode.newBuilder();
        StaticField staticField = (StaticField) Site.getNodeInstance(StaticField.class, sootField);
        builder.setGlobalId(SiteLevelSite.getLevelSiteID(sootField.getName(), sootField.getDeclaringClass().getName()));
        builder.setNode(convertToCodeGraphNode(staticField));
        return builder.build();
    }

    public static StructSchema convertToStructSchema(SootClass sootClass) {
        StructSchema.Builder builder = StructSchema.newBuilder();
        builder.setId(sootClass.getName());
        sootClass.getFields().forEach(sootField -> {
            if (!sootField.isStatic()) {
                builder.addFields(convertToFieldSchema(sootField));
            }
        });
        return builder.build();
    }

    public static FuncSignature convertToFunctionSignature(IntraAnalyzedMethod analyzedMethod) {
        FuncSignature.Builder builder = FuncSignature.newBuilder();
        builder.setClassType(analyzedMethod.getMethodRef().getDeclaringClass().getName());
        for (int i = 0; i < analyzedMethod.getParamTypes().size(); i++) {
            builder.addArgTypes(analyzedMethod.getParamTypes().get(i));
        }
        builder.addRetTypes(analyzedMethod.getMethodRef().getReturnType().toString());
        return builder.build();
    }

    public static Pos convertToPos(String filename, Unit unit) {
        int lineNumber = TagUtil.getUnitLineNumber(unit);
        return Pos.newBuilder().setLine(lineNumber).setFile(filename).build();
    }

    public static FieldSchema convertToFieldSchema(SootField sootField) {
        FieldSchema.Builder builder = FieldSchema.newBuilder();
        builder.setName(sootField.getName());
        builder.setType(sootField.getType().toString());
        return builder.build();
    }

    public static Function convertToFunction(IntraAnalyzedMethod analyzedMethod) {
        Function.Builder builder = Function.newBuilder();
        builder.setId(analyzedMethod.getSignature());
        builder.setName(analyzedMethod.getName());
        builder.setSignature(CodeGraphConverter.convertToFunctionSignature(analyzedMethod));
        builder.setIsIgnore(analyzedMethod.getMethodRef().isAbstract());
        if (!builder.getIsIgnore()) {
            Map<Node, IntraAnalyzedMethod.AnalyzedUnit> flow = analyzedMethod.getOrderedFlowMap();
            CodeGraphFlowConsumer consumer = new CodeGraphFlowConsumer(analyzedMethod);
            flow.forEach(consumer);

            // set variables
            consumer.getVariables().forEach((node, variableSlot) -> builder.addVariables(variableSlot.getVariable()));

            // set statement
            for (int i = 0; i < consumer.getStatementList().size(); i++) {
                builder.addStatements(i, consumer.getStatementList().get(i));
            }
        }
        return builder.build();
    }
}
