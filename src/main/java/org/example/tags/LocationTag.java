package org.example.tags;

import soot.SootMethod;
import soot.Unit;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class LocationTag implements Tag {
    public static final String LocationTag = "LocationTag";
    private SootMethod enclosingMethod;
    private int order;
    private Unit unit;

    public LocationTag(SootMethod sootMethod, int order, Unit unit) {
        this.enclosingMethod = sootMethod;
        this.order = order;
        this.unit = unit;
    }

    @Override
    public String getName() {
        return LocationTag;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return String.format("%s:%d:%s", enclosingMethod.getSignature(), order, unit).getBytes();
    }

    public static String getLocation(Unit unit) {
        Tag tag = unit.getTag(LocationTag);
        if (tag != null) {
            return new String(tag.getValue());
        }
        return unit.toString();
    }
}
