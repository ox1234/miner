package org.example.neo4j.relation;

import org.neo4j.ogm.annotation.Id;

public abstract class AbstractRelation {
    @Id
    private String id;

    public AbstractRelation(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractRelation) {
            return ((AbstractRelation) obj).id.equals(this.id);
        }
        return super.equals(obj);
    }
}
