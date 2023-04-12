package org.example.core.basic;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.core.Loc;

public abstract class MethodLevelSite extends Site {
    private String name;
    private String methodSig; // enclosing method

    protected MethodLevelSite(String name, String methodSig) {
        this.name = name;
        this.methodSig = methodSig;
        super.id = getLevelSiteID(name, methodSig);
    }

    public static String getLevelSiteID(String name, String methodSig) {
        return DigestUtils.sha1Hex(String.format("%s:%s", name, methodSig));
    }

    public String getName() {
        return name;
    }

    public String getMethodSig() {
        return methodSig;
    }

    @Override
    public String toString() {
        return String.format("%s(variable)", name);
    }
}
