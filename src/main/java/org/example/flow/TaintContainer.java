package org.example.flow;

import org.example.config.Global;
import org.example.core.basic.Node;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.obj.Obj;
import org.example.util.PrintUtil;
import soot.Unit;
import soot.jimple.Stmt;

import java.util.*;
import java.util.function.BiConsumer;

public class TaintContainer {
    private boolean isParamTaint;
    private Map<Integer, Boolean> paramTaintMap = new HashMap<>();
    private Set<Obj> taintObjs = new LinkedHashSet<>();
    private static Set<Obj> globalTaintObjs = new LinkedHashSet<>();
    private Set<Node> taintNodes = new LinkedHashSet<>();
    private Map<Unit, Node> inorderedTaintUnit = new LinkedHashMap<>();

    public void addTaint(Node node, Set<Obj> objs, Unit stmt) {
        taintNodes.add(node);

        if (stmt != null) {
            inorderedTaintUnit.put(stmt, node);
        }

        if (node instanceof Parameter) {
            paramTaintMap.put(((Parameter) node).getIdx(), true);
            isParamTaint = true;
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

    public List<String> toTaintFlowUnitStr() {
        List<String> taintUnits = new ArrayList<>();
        inorderedTaintUnit.forEach((unit, node) -> taintUnits.add(String.format("\t%s [%s]", unit, node)));
        return taintUnits;
    }

    public Set<Node> getTaintNodes() {
        return taintNodes;
    }
}
