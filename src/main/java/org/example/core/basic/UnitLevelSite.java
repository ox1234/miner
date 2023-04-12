package org.example.core.basic;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.core.Loc;
import soot.Type;

public abstract class UnitLevelSite extends Site {
    protected String location;
    protected Loc loc;

    protected void setLoc(Loc loc) {
        this.loc = loc;
    }

    protected UnitLevelSite(String location) {
        this.location = location;
        super.id = getLevelSiteID(location);
    }

    protected UnitLevelSite(String type, String location) {
        this.location = location;
        super.id = getLevelSiteID(type, location);
    }

    public static String getLevelSiteID(String location) {
        return DigestUtils.sha1Hex(String.format("%s", location));
    }

    public static String getLevelSiteID(String type, String location) {
        return DigestUtils.sha1Hex(String.format("%s:%s", type, location));
    }
}
