package org.example.core.basic;

import org.apache.commons.codec.digest.DigestUtils;
import soot.Type;

public abstract class ClassLevelSite extends Site {
    private String name;
    private String typeStr;

    protected ClassLevelSite(String name, String type) {
        this.name = name;
        this.typeStr = type;
        super.id = getLevelSiteID(name, type);
    }

    public static String getLevelSiteID(String name, String className) {
        return DigestUtils.sha1Hex(String.format("%s:%s", name, className));
    }

    @Override
    public String toString() {
        return String.format("%s.%s", typeStr, name);
    }
}
