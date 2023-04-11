package org.example.core.basic.node;

import org.example.core.basic.UnitLevelSite;
import org.example.tags.LocationTag;
import soot.Unit;
import soot.VoidType;

public class VoidNode extends UnitLevelSite {
    protected VoidNode(Unit location) {
        super(VoidType.v().toString(), LocationTag.getLocation(location));
    }

    @Override
    public String toString() {
        return "voidnode";
    }
}
