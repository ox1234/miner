package org.example.soot;

import org.example.util.ClassUtil;
import soot.SootClass;

public class DefaultSourceTypeSpecifier implements SourceTypeSpecifier {
    @Override
    public SourceType getClassSourceType(SootClass sootClass) {
        return ClassUtil.getClassSourceType(sootClass);
    }
}
