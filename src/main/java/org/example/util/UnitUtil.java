package org.example.util;

import org.apache.commons.codec.digest.DigestUtils;
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
        return DigestUtils.sha1Hex(String.format("%s-%s", sootMethod.getSignature(), getLineNumber(unit)));
    }
}
