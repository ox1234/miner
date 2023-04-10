package org.example.flow;

import org.example.config.Global;
import org.example.core.basic.Node;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.util.PrintUtil;

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

        if (node instanceof UnifyReturn) {
            isRetTaint = true;
        }

        if (node instanceof Global) {
            globalTaintNodes.add(node);
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

    public void printTaintTable(String methodSig, List<String> callStackArr) {
        List<List<String>> table = new ArrayList<>();
        table.add(Arrays.asList("id", "variable", "node type", "ref stmt"));
        for (Node node : taintNodes) {
            String stmt = null;
            if (node.getRefStmt() != null) {
                stmt = node.getRefStmt().toString();
            } else {
                stmt = "null";
            }
            table.add(Arrays.asList(node.getID(), node.toString(), node.getClass().getSimpleName(), stmt));
        }

        if (Global.debug) {
            PrintUtil.printTable(table, methodSig, callStackArr);
        }
    }
}
