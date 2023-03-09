package org.example.soot.impl;

import org.example.soot.TargetHandler;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class SingleClassHandler implements TargetHandler {
    @Override
    public boolean canHandle(Path path) throws Exception {
        return path.toFile().getName().endsWith(".class");
    }

    @Override
    public List<String> getTargetClassDir(Path path) throws Exception {
        return Collections.singletonList(path.toFile().getParent());
    }

    @Override
    public List<String> getLibraryClassDir(Path path) throws Exception {
        return Collections.emptyList();
    }
}
