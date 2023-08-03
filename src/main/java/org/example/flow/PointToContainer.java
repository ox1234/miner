package org.example.flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.basic.Global;
import org.example.core.basic.Node;
import org.example.core.basic.field.ArrayLoad;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.obj.Obj;
import org.example.util.NodeUtil;

import java.util.*;

public class PointToContainer {
    private final Logger logger = LogManager.getLogger(PointToContainer.class);

    private Map<Node, Set<Obj>> pointoNodes = new LinkedHashMap<>();
    private Set<Obj> retObjs = new LinkedHashSet<>();
    private static Map<Node, Set<Obj>> globalPointoNodes = new LinkedHashMap<>();

    public Set<Obj> getNodeRefObj(Node rightNode) {
        Set<Obj> objs = new LinkedHashSet<>();
        if (rightNode instanceof Obj) {
            // if right node is a object, just return
            objs.add((Obj) rightNode);
        } else if (rightNode instanceof LocalVariable) {
            // if right node is a variable, will get it's object from point container
            objs.addAll(getPointRefObj(rightNode));
        } else if (rightNode instanceof InstanceField) {
            // if right node is instance field(xxx = a.b), will get it own base object and get what object is b referenced
            InstanceField field = (InstanceField) rightNode;
            Set<Obj> baseObjs = getNodeRefObj(field.getBase());
            objs.addAll(handleFieldLoad(baseObjs, Collections.singleton(field)));
        } else if (rightNode instanceof StaticField) {
            // if right node is static field, also check point container
            objs.addAll(getPointRefObj(rightNode));
        } else if (rightNode instanceof ArrayLoad) {
            // if right node is array load(xxx = base[idx]), will get the object the base referenced and idx reference
            ArrayLoad arrLoad = (ArrayLoad) rightNode;
            Set<Obj> baseObjs = getNodeRefObj(arrLoad.getBaseNode());
            Set<Obj> idxObjs = getNodeRefObj(arrLoad.getIdxNode());
            objs.addAll(handleFieldLoad(baseObjs, NodeUtil.convertToNodeSet(idxObjs)));
        }
        return objs;
    }

    private Set<Obj> handleFieldLoad(Set<Obj> baseObjs, Set<Node> fieldNodes) {
        Set<Obj> objs = new LinkedHashSet<>();
        for (Obj baseObj : baseObjs) {
            for (Node fieldNode : fieldNodes) {
                Set<Obj> refObjs = baseObj.getField(fieldNode);
                if (refObjs != null) {
                    objs.addAll(refObjs);
                } else {
                    logger.debug(String.format("%s object %s field is point to any object, will skip tracing", baseObj, fieldNode));
                }
            }
        }
        return objs;
    }

    public void addPointRelation(Node node, Set<Obj> objSet) {
        if (objSet.isEmpty()) {
            return;
        }

        // if is unify return, will set method return objs
        if (node instanceof UnifyReturn) {
            retObjs.addAll(objSet);
        }

        if (node instanceof Global) {
            addGlobalPointRelation(node, objSet);
        } else {
            addLocalPointRelation(node, objSet);
        }
    }

    public Set<Obj> getReturnObjs() {
        return retObjs;
    }

    public void addLocalPointRelation(Node node, Set<Obj> objs) {
        pointoNodes.computeIfAbsent(node, k -> new LinkedHashSet<>());
        pointoNodes.get(node).addAll(objs);
    }

    public void addGlobalPointRelation(Node node, Set<Obj> objs) {
        globalPointoNodes.computeIfAbsent(node, k -> new LinkedHashSet<>());
        globalPointoNodes.get(node).addAll(objs);
    }

    public Set<Obj> getPointRefObj(Node node) {
        Set<Obj> obj = pointoNodes.get(node);
        if (obj == null) {
            obj = globalPointoNodes.get(node);
        }
        if (obj == null) {
            obj = Collections.emptySet();
        }
        return obj;
    }

    public int getPointContainerSize() {
        return pointoNodes.size();
    }
}
