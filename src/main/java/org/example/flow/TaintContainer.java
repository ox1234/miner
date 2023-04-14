package org.example.flow;

import org.example.config.Global;
import org.example.core.basic.Node;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.obj.Obj;
import org.example.util.PrintUtil;

import java.util.*;

public class TaintContainer {
    private boolean isRetTaint;
    private boolean isParamTaint;
    private Map<Integer, Boolean> paramTaintMap = new HashMap<>();
    private Set<Obj> taintObjs = new LinkedHashSet<>();
    private static Set<Obj> globalTaintObjs = new LinkedHashSet<>();
    private Set<Node> taintNodes = new HashSet<>();

    public void addTaint(Node node, Set<Obj> objs) {
        taintNodes.add(node);

        if (node instanceof Parameter) {
            paramTaintMap.put(((Parameter) node).getIdx(), true);
            isParamTaint = true;
        }

        if (node instanceof UnifyReturn) {
            isRetTaint = true;
        }

        if (node instanceof Global) {
            globalTaintObjs.addAll(objs);
        } else {
            taintObjs.addAll(objs);
        }
    }

    public boolean checkIdxParamIsTaint(int idx) {
        return paramTaintMap.get(idx) != null;
    }

    public boolean isParamTaint() {
        return isParamTaint;
    }

    public boolean containsTaint(Obj obj) {
        return taintObjs.contains(obj) || globalTaintObjs.contains(obj);
    }

    public boolean containsTaint(Set<Obj> objs) {
        for (Obj obj : objs) {
            if (containsTaint(obj)) {
                return true;
            }
        }
        return false;
    }

    public void printTaintTable(String methodSig, List<String> callStackArr) {
        List<List<String>> table = new ArrayList<>();
        table.add(Arrays.asList("id", "variable", "node type", "ref stmt"));
        for (Node node : taintObjs) {
//            String stmt = null;
//            if (node.getLoc() != null) {
//                stmt = node.getLoc().toString();
//            } else {
//                stmt = "null";
//            }
//            table.add(Arrays.asList(node.getID(), node.toString(), node.getClass().getSimpleName(), stmt));
        }

        if (Global.debug) {
            PrintUtil.printTable(table, methodSig, callStackArr);
        }
    }
}
