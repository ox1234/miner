package org.example.neo4j.node;

import org.example.config.Global;
import org.neo4j.ogm.annotation.NodeEntity;
import soot.SootMethod;

import java.util.ArrayList;

@NodeEntity
public class SinkMethod extends Method {
    boolean isSink = true;


}
