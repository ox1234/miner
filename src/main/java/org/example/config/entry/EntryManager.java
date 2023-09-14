package org.example.config.entry;

import soot.SootClass;
import soot.SootMethod;

import java.util.Set;

public interface EntryManager {
    Set<IEntry> getEntry(SootClass sootClass);
}
