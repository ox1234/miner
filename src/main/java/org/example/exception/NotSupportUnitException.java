package org.example.exception;

import soot.Unit;

public class NotSupportUnitException extends Exception {
    public NotSupportUnitException(Unit unit) {
        super(String.format("%s unit is not support", unit));
    }
}
