package org.example.neo4j.relation;

import org.example.neo4j.node.Method;
import org.neo4j.ogm.annotation.*;

import java.util.List;

@RelationshipEntity(type = "CALL")
public class Call {
    @Id
    @GeneratedValue
    Long id;

    List<String> args;
    List<String> argTypes;

    @StartNode
    Method src;

    @EndNode
    Method tgt;
}
