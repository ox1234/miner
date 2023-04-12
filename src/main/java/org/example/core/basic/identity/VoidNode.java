package org.example.core.basic.identity;

import org.example.core.basic.UnitLevelSite;
import soot.VoidType;

import java.util.UUID;

public class VoidNode extends UnitLevelSite {
    protected VoidNode() {
        super(VoidType.v().toString(), UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return VoidType.v().toString();
    }
}
