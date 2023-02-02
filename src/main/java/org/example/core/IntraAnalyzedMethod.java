package org.example.core;

import org.example.config.NodeRepository;
import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.node.CallNode;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntraAnalyzedMethod {
    // method base information
    private String name;
    private String signature;
    private String subSignature;
    private int numberOfParams;
    private List<String> paramTypes = new ArrayList<>();
    private SootMethod methodRef;
    private SootClass declaredClassRef;

    // method inner allocation site
    Set<MethodLevelSite> methodLevelSiteSet;
    List<CallNode> callees;

    public IntraAnalyzedMethod(SootMethod sootMethod) {
        this.name = sootMethod.getName();
        this.signature = sootMethod.getSignature();
        this.subSignature = sootMethod.getSubSignature();
        this.numberOfParams = sootMethod.getParameterCount();
        for (int i = 0; i < sootMethod.getParameterCount(); i++) {
            this.paramTypes.add(sootMethod.getParameterType(i).toString());
        }
        this.methodRef = sootMethod;
        this.declaredClassRef = sootMethod.getDeclaringClass();

        methodLevelSiteSet = NodeRepository.getMethodNodes(sootMethod.getSignature());
        callees = NodeRepository.getMethodCallees(sootMethod.getSignature());
    }

    public String getDeclaredClassName() {
        return this.declaredClassRef.getName();
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public String getSubSignature() {
        return subSignature;
    }

    public int getNumberOfParams() {
        return numberOfParams;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public SootMethod getMethodRef() {
        return methodRef;
    }

    public Set<MethodLevelSite> getMethodLevelSiteSet() {
        return methodLevelSiteSet;
    }
}
