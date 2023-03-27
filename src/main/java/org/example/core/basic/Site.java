package org.example.core.basic;

import org.example.config.NodeRepository;
import org.example.core.basic.obj.Obj;
import org.example.util.Log;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class Site implements Node {
    protected String id;
    protected Obj refObj;
    protected Stmt refStmt;

    public String getID() {
        return id;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Site)) {
            return false;
        }
        Site site = (Site) obj;
        return site.getID().equals(this.getID());
    }

    public static Node getNodeInstance(Class<?> nodeClass, Object... args) {
        try {
            Constructor<?>[] constructors = nodeClass.getDeclaredConstructors();
            assert constructors.length == 1;
            Constructor<?> constructor = constructors[0];
            constructor.setAccessible(true);
            Node node = (Node) constructor.newInstance(args);
            return NodeRepository.addNode(node);
        } catch (InvocationTargetException | InstantiationException | IllegalArgumentException |
                 IllegalAccessException e) {
            Log.error("construct %s class instance fail", nodeClass.getName());
        }
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Obj getRefObj() {
        return refObj;
    }

    @Override
    public void setRefObj(Obj refObj) {
        this.refObj = refObj;
    }

    @Override
    public void setRefStmt(Stmt stmt) {
        refStmt = stmt;
    }

    @Override
    public Stmt getRefStmt() {
        return refStmt;
    }
}
