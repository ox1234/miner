package org.example.config;

import org.example.core.IntraAnalyzedMethod;

import java.util.HashSet;
import java.util.Set;

public class AnalysisResult {
    private static Set<IntraAnalyzedMethod> analyzedMethods = new HashSet<>();

    public static void addAnalyzedMethod(IntraAnalyzedMethod method) {
        analyzedMethods.add(method);
    }
}
