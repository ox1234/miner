package org.example.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.constant.MyBatis;
import org.example.extra.MybatisXMLMapperHandler;
import org.example.util.TagUtil;
import soot.SootMethod;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBatisIntraAnalyzedMethod extends IntraAnalyzedMethod {
    private final Logger logger = LogManager.getLogger(MyBatisIntraAnalyzedMethod.class);
    private List<String> sqlExprs;
    private Map<String, Integer> sqlParamMap;
    private Set<Integer> injectedParamIdxs;

    public MyBatisIntraAnalyzedMethod(SootMethod sootMethod) {
        super(sootMethod);
        injectedParamIdxs = new HashSet<>();
        sqlParamMap = TagUtil.getMyBatisParamAnnotationValue(sootMethod);

        if (TagUtil.isMybatisSelectMethod(sootMethod)) {
            handleSelectMethod(sootMethod);
        } else {
            handleMapperMethod(sootMethod);
        }
    }

    private void handleMapperMethod(SootMethod sootMethod) {
        for (String sqlExpr : MybatisXMLMapperHandler.getPlaceHolder(sootMethod.getName())) {
            mapSqlExpr(sqlExpr);
        }
    }

    private void handleSelectMethod(SootMethod sootMethod) {
        sqlExprs = TagUtil.getMybatisSelectAnnotationValue(sootMethod);
        for (String sqlExpr : sqlExprs) {
            mapSqlExpr(sqlExpr);
        }
    }

    private void mapSqlExpr(String sqlExpr) {
        Matcher matcher = MyBatis.mybatisPlaceHolderPattern.matcher(sqlExpr);
        if (matcher.find()) {
            String sqlParamName = matcher.group(1);
            if (sqlParamMap.containsKey(sqlParamName)) {
                injectedParamIdxs.add(sqlParamMap.get(sqlParamName));
            } else if (sqlParamName.equals("_parameter")) {
                injectedParamIdxs.add(0);
            } else {
                try {
                    int idx = Integer.parseInt(sqlParamName, 10);
                    injectedParamIdxs.add(idx);
                } catch (NumberFormatException e) {
                    logger.error(String.format("%s sql's %s parameter is not a valid param idx used in mybatis", sqlExpr, sqlParamName));
                }
            }
        }
    }

    public List<String> getSqlExprs() {
        return sqlExprs;
    }

    public Set<Integer> getInjectedParamIdxs() {
        return injectedParamIdxs;
    }
}
