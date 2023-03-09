package org.example.core.basic;

import org.example.core.basic.obj.Obj;

public interface Node {
    String getID();

    void setRefObj(Obj obj);

    Obj getRefObj();
}
