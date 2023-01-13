package org.example.neo4j.service;

import org.example.config.Global;
import org.example.neo4j.node.method.Method;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class MethodService {
    final SessionFactory sessionFactory;

    public MethodService() {
        Configuration config = new Configuration.Builder().uri(Global.neo4jDSN).credentials(Global.neo4jUser, Global.neo4jPass).build();
        this.sessionFactory = new SessionFactory(config, "org.example.neo4j");
    }

    public void createMethodNode(Method method) {
        Session session = sessionFactory.openSession();
        session.save(method);
    }
}
