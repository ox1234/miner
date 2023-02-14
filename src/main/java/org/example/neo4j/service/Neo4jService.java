package org.example.neo4j.service;

import org.example.config.Global;
import org.example.config.NodeRepository;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.relation.*;
import org.example.util.Log;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.*;

public class Neo4jService {
    private final SessionFactory sessionFactory;

    public Neo4jService() {
        // 设置neo4j
        Configuration config = new Configuration.Builder().uri(Global.neo4jDSN).credentials(Global.neo4jUser, Global.neo4jPass).build();
        this.sessionFactory = new SessionFactory(config, "org.example.neo4j");
    }


    public void saveRelation(AbstractRelation relation) {
        Session session = sessionFactory.openSession();
        session.save(relation);
    }

    public void saveRelation(Collection<AbstractRelation> relations) {
        relations.forEach(this::saveRelation);
    }
}
