package org.example.core.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.NodeRepository;
import soot.jimple.Stmt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Site implements Node {
    private static final Logger logger = LogManager.getLogger(Site.class);
    protected String id;
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
            logger.error(String.format("construct %s class instance fail", nodeClass.getName()));
        }
        return null;
    }

    public void setId(String id) {
        this.id = id;
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
