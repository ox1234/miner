package org.example.neo4j.node.var;

import org.example.config.FlowRepository;
import org.example.config.NodeRepository;
import org.example.core.basic.Node;
import org.example.core.basic.identity.Identity;
import org.example.core.basic.obj.Obj;

public class VarAllocNode extends AbstractAllocNode {
    public String name;

    protected VarAllocNode(Identity node) {
        super(node);
        this.name = node.getName();

        for (String fromNodeID : FlowRepository.getTaintFlow(node.getNodeID())) {
            Node fromNode = NodeRepository.getNode(fromNodeID);
            if (fromNode == null) {
                continue;
            }
            AbstractAllocNode allocNode = AbstractAllocNode.getInstance(fromNode);
            if (allocNode == null) {
                continue;
            }
            if (allocNode instanceof ObjAllocNode) {
                alias.add(allocNode);
            } else {
                taints.add(allocNode);
            }
        }
    }
}
