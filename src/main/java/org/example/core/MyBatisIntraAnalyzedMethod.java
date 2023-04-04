package org.example.core;

import org.example.util.TagUtil;
import soot.SootMethod;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBatisIntraAnalyzedMethod extends IntraAnalyzedMethod {
    private Pattern mybatisPlaceHolderPattern;
    private List<String> sqlExprs;
    private Map<String, Integer> sqlParamMap;
    private Set<Integer> injectedParamIdxs;

    public MyBatisIntraAnalyzedMethod(SootMethod sootMethod) {
        super(sootMethod);
        injectedParamIdxs = new HashSet<>();
        mybatisPlaceHolderPattern = Pattern.compile("\\$\\{([a-z0-9_]*)\\}");
        sqlParamMap = TagUtil.getMyBatisParamAnnotationValue(sootMethod);

        sqlExprs = TagUtil.getMybatisSelectAnnotationValue(sootMethod);
        for (String sqlExpr : sqlExprs) {
            Matcher matcher = mybatisPlaceHolderPattern.matcher(sqlExpr);
            if (matcher.find()) {
                String sqlParamName = matcher.group(1);
                if (sqlParamMap.containsKey(sqlParamName)) {
                    injectedParamIdxs.add(sqlParamMap.get(sqlParamName));
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
