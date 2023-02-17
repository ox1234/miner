package org.example.core.basic.node;

import org.example.core.basic.UnitLevelSite;
import org.example.tags.LocationTag;
import soot.Unit;

public class VoidNode extends UnitLevelSite {
    protected VoidNode(Unit location) {
        super("void", LocationTag.getLocation(location));
    }
}
