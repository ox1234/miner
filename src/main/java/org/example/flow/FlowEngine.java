package org.example.flow;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import org.example.flow.handler.impl.NodeCallNodeHandler;
import org.example.flow.handler.impl.NodeObjHandler;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import soot.*;

import java.util.*;

public class FlowEngine {
    private List<AbstractFlowHandler> flowHandlers;

    private Map<String, IntraAnalyzedMethod> analyzedMethodMap;
    private Graph<SootMethod, CallNode> cg = new DefaultDirectedGraph<>(CallNode.class);

    private CallStack callStack;
    private Hierarchy hierarchy;

    public FlowEngine(Map<String, IntraAnalyzedMethod> analyzedMethodMap) {
        this.analyzedMethodMap = analyzedMethodMap;
        this.callStack = new CallStack();
        this.hierarchy = Scene.v().getActiveHierarchy();

        this.flowHandlers = new ArrayList<>();
        registerFlowHandler(new NodeObjHandler(this));
        registerFlowHandler(new NodeCallNodeHandler(this));
    }

    public void registerFlowHandler(AbstractFlowHandler flowHandler) {
        flowHandlers.add(flowHandler);
    }

    public void traverse(SootMethod entryPoint) {
        Obj fakeObj = new PhantomObj(entryPoint.getDeclaringClass());
        ContextMethod entry = new InstanceContextMethod(fakeObj, entryPoint, null, null);
        traverse(entry);
    }

    public boolean traverse(ContextMethod entry) {
        callStack.push(entry);
        IntraAnalyzedMethod analyzedMethod = analyzedMethodMap.get(entry.getSootMethod().getSignature());
        if (analyzedMethod != null) {
            // add method has relation
            analyzedMethod.getOrderedFlowMap().forEach((node, abstractExprNode) -> {
                for (AbstractFlowHandler flowHandler : flowHandlers) {
                    if (flowHandler.canHandle(node, abstractExprNode)) {
                        flowHandler.injectBelongMethod(entry);
                        flowHandler.handle(node, abstractExprNode);
                        break;
                    }
                }
            });
        }
        callStack.pop();
        return entry.returnTaint();
    }

    public void addCGEdge(CallNode callNode) {
        SootMethod srcMethod = callNode.getCaller();
        SootMethod tgtMethod = callNode.getCallee();
        cg.addVertex(srcMethod);
        cg.addVertex(tgtMethod);
        cg.addEdge(srcMethod, tgtMethod, callNode);
    }


    public CallStack getCallStack() {
        return callStack;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }
}
