package org.example.flow;

import org.example.flow.context.ContextMethod;

public interface Collector {
    void collect(CallStack callStack);
}
