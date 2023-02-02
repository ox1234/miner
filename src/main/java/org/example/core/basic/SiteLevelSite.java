package org.example.core.basic;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class SiteLevelSite extends Site {
    private String baseID;
    private String name;

    protected SiteLevelSite(String baseID, String name) {
        this.baseID = baseID;
        this.name = name;
        super.id = getLevelSiteID(baseID, name);
    }

    public static String getLevelSiteID(String baseID, String name) {
        return DigestUtils.sha1Hex(String.format("%s:%s", baseID, name));
    }

    public String getBaseID() {
        return baseID;
    }
}
