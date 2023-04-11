package org.example.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.constant.MyBatis;
import org.example.core.basic.Node;
import org.example.core.basic.obj.Obj;
import org.example.extra.MybatisXMLMapperHandler;
import org.example.flow.context.ContextMethod;
import org.example.util.TagUtil;
import soot.SootMethod;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBatisIntraAnalyzedMethod extends IntraAnalyzedMethod {
    private final Logger logger = LogManager.getLogger(MyBatisIntraAnalyzedMethod.class);
    private List<String> sqlExprs;
    private Map<String, Integer> sqlParamMap;
    private List<String> placeHolderList = new ArrayList<>();

    public MyBatisIntraAnalyzedMethod(SootMethod sootMethod) {
        super(sootMethod);
        sqlParamMap = TagUtil.getMyBatisParamAnnotationValue(sootMethod);

        if (TagUtil.isMybatisSelectMethod(sootMethod)) {
            handleSelectMethod(sootMethod);
        } else {
            handleMapperMethod(sootMethod);
        }
    }

    public Map<String, Integer> getSqlParamMap() {
        return sqlParamMap;
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
        while (matcher.find()) {
            String sqlParamName = matcher.group(1).split(",")[0];
            placeHolderList.add(sqlParamName);
        }
    }

    public List<String> getPlaceHolderList() {
        return placeHolderList;
    }
}
