package org.example.flow.collector.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.basic.Node;
import org.example.flow.CallStack;
import org.example.flow.TaintContainer;
import org.example.flow.collector.Collector;
import org.example.flow.collector.vuln.VulnCollector;
import org.example.flow.context.ContextMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TaintVarCollector implements Collector {
    private final Logger logger = LogManager.getLogger(TaintVarCollector.class);

    @Override
    public void collect(CallStack callStack) {
        ContextMethod reachedMethod = callStack.peek();
        Set<Node> taintNodes = reachedMethod.getTaintContainer().getTaintNodes();
        logger.debug(String.format("%s method has following taint: [%s]", reachedMethod.getSootMethod().getSignature(), String.join(",", toNodesStr(taintNodes))));
    }

    private List<String> toNodesStr(Set<Node> nodes) {
        List<String> nodesStr = new ArrayList<>();
        nodes.forEach(node -> nodesStr.add(node.toString()));
        return nodesStr;
    }
}
