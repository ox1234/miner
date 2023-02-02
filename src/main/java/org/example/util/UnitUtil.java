package org.example.util;

import org.apache.commons.codec.digest.DigestUtils;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;

public class UnitUtil {
    public static int getLineNumber(Unit unit) {
        for (Tag tag : unit.getTags()) {
            if (tag instanceof LineNumberTag) {
                return ((LineNumberTag) tag).getLineNumber();
            }
        }
        return -1;
    }

    public static String getUniqHash(SootMethod sootMethod, Unit unit) {
        return DigestUtils.sha1Hex(String.format("%s-%s-%s", sootMethod.getSignature(), unit, getLineNumber(unit)));
    }

    public static String getUniqHash(String sig, Unit unit) {
        return DigestUtils.sha1Hex(String.format("%s-%s-%s", sig, unit, getLineNumber(unit)));
    }

    public static String getCallNodeID(SootMethod enclosingMethod, Unit nodeSite) {
        return String.format("call-%s", UnitUtil.getUniqHash(enclosingMethod, nodeSite));
    }

    public static String getObjNodeID(SootMethod sootMethod, Unit unit) {
        return String.format("obj-%s", UnitUtil.getUniqHash(sootMethod, unit));
    }

    public static String getIdentityNodeID(SootMethod sootMethod, String name) {
        String methodSig = sootMethod.getSignature();
        return String.format("%s", DigestUtils.sha1Hex(String.format("%s-%s", methodSig, name)));
    }

    public static String getFieldNodeID(SootClass sootClass, String fieldName) {
        return String.format("%s", DigestUtils.sha1Hex(String.format("%s-%s", sootClass.getName(), fieldName)));
    }

    public static String getParameterNodeID(SootMethod sootMethod, int idx) {
        return getIdentityNodeID(sootMethod, String.format("param-%s-%d", sootMethod.getSignature(), idx));
    }
}
