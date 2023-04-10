package org.example.soot.impl;

import org.example.extra.IResourceHandler;
import org.example.soot.TargetHandler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTargetHandler implements TargetHandler {
    private List<IResourceHandler> resourceHandlerList = new ArrayList<>();

    public void registerHandler(IResourceHandler resourceHandler) {
        resourceHandlerList.add(resourceHandler);
    }

    public void apply(Path path) {
        for (IResourceHandler resourceHandler : resourceHandlerList) {
            resourceHandler.handle(path);
        }
    }
}
