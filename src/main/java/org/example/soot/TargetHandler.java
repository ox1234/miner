package org.example.soot;

import java.nio.file.Path;
import java.util.List;

public interface TargetHandler {
    boolean canHandle(Path path) throws Exception;

    List<String> getTargetClassDir(Path path) throws Exception;

    List<String> getLibraryClassDir(Path path) throws Exception;
}
