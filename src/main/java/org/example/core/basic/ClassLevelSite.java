package org.example.core.basic;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class ClassLevelSite extends Site {
    private String name;
    private String className;

    protected ClassLevelSite(String name, String className) {
        this.name = name;
        this.className = className;
        super.id = getLevelSiteID(name, className);
    }

    public static String getLevelSiteID(String name, String className) {
        return DigestUtils.sha1Hex(String.format("%s:%s", name, className));
    }

    @Override
    public String toString() {
        return String.format("[%s].%s", className, name);
    }
}
