package org.example.core.hook.bytedance;

import org.example.core.IntraAnalyzedMethod;
import soot.SootClass;

public interface EngineHook {
    boolean enabled();

    void hookClass(SootClass sootClass);

    void hookMethod(IntraAnalyzedMethod analyzedMethod);

    void engineFinish();
}
