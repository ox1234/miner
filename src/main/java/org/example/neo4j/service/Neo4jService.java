package org.example.neo4j.service;

import org.example.config.FlowRepository;
import org.example.config.Global;
import org.example.core.IntraAnalyzedMethod;
import org.example.neo4j.node.method.AbstractMethod;
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
    private final CallGraph cg;
    private final Map<String, IntraAnalyzedMethod> methodMap = new HashMap<>();

    public Neo4jService(CallGraph cg, Collection<IntraAnalyzedMethod> intraAnalyzedMethods) {
        // 设置neo4j
        Configuration config = new Configuration.Builder().uri(Global.neo4jDSN).credentials(Global.neo4jUser, Global.neo4jPass).build();
        this.sessionFactory = new SessionFactory(config, "org.example.neo4j");

        // 存储cg和过程间分析结果
        this.cg = cg;
        for (IntraAnalyzedMethod intraAnalyzedMethod : intraAnalyzedMethods) {
            methodMap.put(intraAnalyzedMethod.getSignature(), intraAnalyzedMethod);
        }
    }


    public Set<AbstractRelation> buildRelations() {
        Set<AbstractRelation> relations = new HashSet<>();
        cg.forEach(edge -> {
            if (edge.tgt() == null || !edge.src().getDeclaringClass().isApplicationClass()) {
                return;
            }

            SootMethod srcMethod = edge.src().method();
            SootMethod tgtMethod = edge.tgt().method();
            Stmt callSite = edge.srcStmt();
            Log.info("call site %s -> %s", srcMethod.getSignature(), tgtMethod.getSignature());

            relations.addAll(Call.getRelations(srcMethod, tgtMethod, callSite));

            // import src method has relation
            IntraAnalyzedMethod srcAnalyzedMethod = methodMap.get(srcMethod.getSignature());
            if (srcAnalyzedMethod != null) {
                relations.addAll(getIntraAnalysisRelation(srcAnalyzedMethod));
            } else {
                relations.addAll(getLibraryRelation(srcMethod));
            }

            // import tgt method has relation
            IntraAnalyzedMethod tgtAnalyzedMethod = methodMap.get(tgtMethod.getSignature());
            if (tgtAnalyzedMethod != null) {
                relations.addAll(getIntraAnalysisRelation(tgtAnalyzedMethod));
            } else {
                relations.addAll(getLibraryRelation(tgtMethod));
            }

            // import taint relation
            relations.addAll(Taint.getRelations(FlowRepository.getTaintFlowMap()));

            // import point relation
            relations.addAll(PointTo.getRelations(FlowRepository.getPointoMap()));

            relations.addAll(CallRet.getRelations(FlowRepository.getCallReturnMap()));

        });
        return relations;
    }

    public Set<AbstractRelation> getIntraAnalysisRelation(IntraAnalyzedMethod intraAnalyzedMethod) {
        Set<AbstractRelation> relations = new HashSet<>();
        // get has relation
        relations.addAll(HasVar.getRelations(intraAnalyzedMethod));
        return relations;
    }

    public Set<AbstractRelation> getLibraryRelation(SootMethod sootMethod) {
        Set<AbstractRelation> relations = new HashSet<>();
        relations.addAll(HasVar.getRelations(sootMethod));
        return relations;
    }

    public void saveRelation(AbstractRelation relation) {
        Session session = sessionFactory.openSession();
        session.save(relation);
    }

    public void saveRelation(Collection<AbstractRelation> relations) {
        relations.forEach(this::saveRelation);
    }
}
