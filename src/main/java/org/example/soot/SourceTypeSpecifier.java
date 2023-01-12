package org.example.soot;

import soot.SootClass;

public interface SourceTypeSpecifier {
    public SourceType getClassSourceType(SootClass sootClass);
}
