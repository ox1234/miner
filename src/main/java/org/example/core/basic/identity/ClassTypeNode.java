package org.example.core.basic.identity;

import org.example.core.basic.ClassLevelSite;
import soot.Type;

public class ClassTypeNode extends ClassLevelSite {
    protected ClassTypeNode(Type type) {
        super(type.toString(), type.toString());
    }
}
