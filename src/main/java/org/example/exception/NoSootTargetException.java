package org.example.exception;

public class NoSootTargetException extends Exception {
    public NoSootTargetException() {
        super("not target to scan");
    }
}
