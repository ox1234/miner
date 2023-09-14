package org.example.flow.collector.vuln;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.entry.RouterEntry;
import org.example.basic.Vulnerability;
import org.example.config.Configuration;
import org.example.config.sourcesink.Sink;
import org.example.config.sourcesink.SourceSinkManager;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.MyBatisIntraAnalyzedMethod;
import org.example.core.RouteIntraAnalyzedMethod;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.obj.NormalObj;
import org.example.core.basic.obj.Obj;
import org.example.flow.CallStack;
import org.example.flow.collector.Collector;
import org.example.flow.PointToContainer;
import org.example.flow.TaintContainer;
import org.example.flow.context.ContextMethod;
import org.example.util.MethodUtil;
import soot.PrimType;
import soot.RefType;
import soot.Type;

import java.util.*;

public class VulnCollector implements Collector {
    private final Logger logger = LogManager.getLogger(VulnCollector.class);
    private Set<Vulnerability> vulnerabilities = new HashSet<>();
    private SourceSinkManager sourceSinkManager = Configuration.getSourceSinkManager();

    @Override
    public void collect(CallStack callStack) {
        ContextMethod reachedMethod = callStack.peek();
        if (checkReachSink(reachedMethod)) {
            ContextMethod entryMethod = callStack.getFirst();
            IntraAnalyzedMethod entryAnalyzedMethod = entryMethod.getIntraAnalyzedMethod();
            RouterEntry routerEntry = null;
            if (entryAnalyzedMethod instanceof RouteIntraAnalyzedMethod) {
                RouteIntraAnalyzedMethod routeIntraAnalyzedMethod = (RouteIntraAnalyzedMethod) entryAnalyzedMethod;
                routerEntry = routeIntraAnalyzedMethod.getRouter();
            }
            vulnerabilities.add(new Vulnerability(routerEntry, reachedMethod, callStack));
            logger.error(String.format("!!! find vulnerability reach sink to %s", reachedMethod.getSootMethod().getSignature()));
        }
    }

    public Set<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public boolean checkReachSink(ContextMethod reachedMethod) {
        IntraAnalyzedMethod analyzedMethod = reachedMethod.getIntraAnalyzedMethod();
        TaintContainer taintContainer = reachedMethod.getTaintContainer();

        // check mybatis sql injection
        if (analyzedMethod instanceof MyBatisIntraAnalyzedMethod) {
            MyBatisIntraAnalyzedMethod myBatisIntraAnalyzedMethod = (MyBatisIntraAnalyzedMethod) analyzedMethod;
            List<String> taintPlaceHolders = new ArrayList<>();
            for (String placeHolder : myBatisIntraAnalyzedMethod.getPlaceHolderList()) {
                if (isTaintMyBatisPlaceHolder(reachedMethod, placeHolder, myBatisIntraAnalyzedMethod.getSqlParamMap())) {
                    taintPlaceHolders.add(placeHolder);
                }
            }

            if (!taintPlaceHolders.isEmpty()) {
                return true;
            }
        }

        // test case sink match
        if (reachedMethod.getSootMethod().getName().equals("sink") && taintContainer.checkIdxParamIsTaint(0)) {
            return true;
        }

        for (String signature : MethodUtil.getOverrideMethodSignatureOfInclude(reachedMethod.getSootMethod())) {
            if (sourceSinkManager.isSinkSig(signature)) {
                Sink sink = sourceSinkManager.getSink(signature);

                // if base is taint and config defined such sink without no param, will report
                if (reachedMethod.isBaseTaint() && sink.getSinkIdx().isEmpty()) {
                    return true;
                }

                // check index sink is taint
                for (int idx : sink.getSinkIdx()) {
                    if (taintContainer.checkIdxParamIsTaint(idx)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isTaintMyBatisPlaceHolder(ContextMethod reachedMethod, String placeHolder, Map<String, Integer> paramAlias) {
        PointToContainer pointToContainer = reachedMethod.getPointToContainer();
        TaintContainer taintContainer = reachedMethod.getTaintContainer();
        IntraAnalyzedMethod analyzedMethod = reachedMethod.getIntraAnalyzedMethod();

        // check if @Param annotation is exists, if exist will map the parameter
        if (paramAlias.containsKey(placeHolder)) {
            return taintContainer.checkIdxParamIsTaint(paramAlias.get(placeHolder));
        }

        // check if is ${_parameter} pattern, will map the first parameter
        if (placeHolder.equals("_parameter")) {
            return taintContainer.checkIdxParamIsTaint(0);
        }

        // check if is ${0} pattern, will map the parameter index
        try {
            int idx = Integer.parseInt(placeHolder, 10);
            return taintContainer.checkIdxParamIsTaint(idx);
        } catch (NumberFormatException ignored) {
        }

        // single parameter handle
        if (analyzedMethod.getParameterNodes().size() == 1) {
            Parameter paramNode = analyzedMethod.getParameterNodes().get(0);
            Type paramType = paramNode.getType();
            // if param is prim type, won't be taint
            if (paramType instanceof PrimType) {
                return false;
            } else if (paramType instanceof RefType) {
                // if param is string type, will check taint container
                if (((RefType) paramType).getSootClass().getName().equals("java.lang.String")) {
                    return taintContainer.checkIdxParamIsTaint(0);
                }

                // if param is other type, possibly a POJO, will check field
                Set<Obj> objs = pointToContainer.getPointRefObj(paramNode);
                for (Obj perObj : objs) {
                    if (perObj instanceof NormalObj) {
                        for (Obj fieldObj : perObj.getField(placeHolder)) {
                            if (taintContainer.containsTaint(fieldObj)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
