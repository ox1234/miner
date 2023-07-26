package org.example.core.processor.bytedance;

import org.example.core.IntraAnalyzedMethod;
import soot.SootClass;

public interface EngineHook {
    void hookClass(SootClass sootClass);

    void hookMethod(IntraAnalyzedMethod analyzedMethod);

    void engineFinish();
}
