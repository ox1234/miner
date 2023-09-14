package org.example.config.entry;

import soot.SootMethod;

public interface IEntry {
    SootMethod entryMethod();

    boolean isParamTaint();
}
