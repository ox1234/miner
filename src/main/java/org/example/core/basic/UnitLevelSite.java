package org.example.core.basic;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class UnitLevelSite extends Site {
    protected String type;
    protected String location;

    protected UnitLevelSite(String type, String location) {
        this.type = type;
        this.location = location;
        super.id = getLevelSiteID(type, location);
    }

    public static String getLevelSiteID(String type, String location) {
        return DigestUtils.sha1Hex(String.format("%s:%s", type, location));
    }
}
