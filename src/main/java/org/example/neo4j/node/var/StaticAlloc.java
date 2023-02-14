package org.example.neo4j.node.var;

import soot.SootClass;

public class StaticAlloc extends AbstractAllocNode {
    public StaticAlloc(SootClass sootClass) {
        super(sootClass.getName(), sootClass.getName());
    }
}
