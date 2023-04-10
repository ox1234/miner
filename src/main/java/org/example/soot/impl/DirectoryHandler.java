package org.example.soot.impl;

import org.example.soot.TargetHandler;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class DirectoryHandler extends AbstractTargetHandler {
    @Override
    public boolean canHandle(Path path) throws Exception {
        return path.toFile().isDirectory();
    }

    @Override
    public List<String> getTargetClassDir(Path path) throws Exception {
        return Collections.singletonList(path.toFile().getAbsolutePath());
    }

    @Override
    public List<String> getLibraryClassDir(Path path) throws Exception {
        return Collections.emptyList();
    }
}
