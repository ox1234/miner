package org.example.neo4j.service;

import org.example.neo4j.node.Method;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import soot.SootMethod;

import java.util.Map;
import java.util.Optional;

public class MethodService {
    final SessionFactory sessionFactory;

    public MethodService() {
        Configuration config = new Configuration.Builder().uri("neo4j://localhost:7687").credentials("neo4j", "password").build();

        this.sessionFactory = new SessionFactory(config, "org.example.neo4j");
    }

    public void createMethodNode(Method method) {
        Session session = sessionFactory.openSession();
        session.save(method);
    }
}
