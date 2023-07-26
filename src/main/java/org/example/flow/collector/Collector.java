package org.example.flow.collector;

import org.example.flow.CallStack;
import org.example.flow.context.ContextMethod;

public interface Collector {
    void collect(CallStack callStack);
}
