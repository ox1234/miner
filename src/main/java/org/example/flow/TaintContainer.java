package org.example.flow;

import org.example.core.basic.Global;
import org.example.core.basic.Node;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;

import java.util.*;

public class TaintContainer {
    private boolean isRetTaint;
    private boolean isParamTaint;
    private Map<Integer, Node> paramTaintMap = new HashMap<>();
    private Set<Node> taintNodes = new LinkedHashSet<>();
    private static Set<Node> globalTaintNodes = new LinkedHashSet<>();

    public void addTaint(Node node) {
        if (node instanceof Parameter) {
            paramTaintMap.put(((Parameter) node).getIdx(), node);
            isParamTaint = true;
        }

        if (node instanceof Global) {
            globalTaintNodes.add(node);
        } else if (node instanceof UnifyReturn) {
            isRetTaint = true;
        } else {
            taintNodes.add(node);
        }
    }

    public boolean checkIdxParamIsTaint(int idx) {
        return paramTaintMap.get(idx) != null;
    }

    public boolean isParamTaint() {
        return isParamTaint;
    }

    public boolean isRetTaint() {
        return isRetTaint;
    }

    public boolean containsTaint(Node node) {
        return taintNodes.contains(node) || globalTaintNodes.contains(node);
    }

    public static void addGlobalTaint(Node node) {
        globalTaintNodes.add(node);
    }

    @Override
    public String toString() {
        List<String> nodeStr = new ArrayList<>();
        for (Node taintNode : taintNodes) {
            nodeStr.add(taintNode.toString());
        }
        return String.format("[%s]", String.join(", ", nodeStr));
    }
}
