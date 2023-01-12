package org.example.core.basic.identity;

import org.example.core.basic.AbstractNode;
import org.example.util.MethodUtil;
import soot.SootMethod;
import soot.Unit;

import java.util.Set;

public abstract class Identity extends AbstractNode {
    public String name;
    public boolean isTaint;

    public Identity(String name, String type, SootMethod enclosingMethod, Unit nodeSite) {
        super(type, enclosingMethod, nodeSite);
        super.id = MethodUtil.getMethodLocalID(enclosingMethod, name);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
