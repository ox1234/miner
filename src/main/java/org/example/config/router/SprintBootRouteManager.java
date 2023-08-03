package org.example.config.router;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Configuration;
import org.example.config.sourcesink.SourceSinkManager;
import org.example.util.TagUtil;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SprintBootRouteManager implements RouteManager {
    private final Logger logger = LogManager.getLogger(SourceSinkManager.class);

    private Path configPath;
    private Set<String> controllerTag;
    private Set<String> routeMethodTag;

    public SprintBootRouteManager(Path runtimeConfPath) {
        this.controllerTag = new HashSet<>();
        this.routeMethodTag = new HashSet<>();
        this.configPath = runtimeConfPath.resolve("spring_route.json");

        loadSpringRouteConfigFromFile(configPath);
    }

    @Override
    public boolean isRouteMethod(SootMethod sootMethod) {
        for (AnnotationTag methodTag : TagUtil.getMethodAnnotation(sootMethod)) {
            if (routeMethodTag.contains(methodTag.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isController(SootClass sootClass) {
        for (AnnotationTag annotationTag : TagUtil.getClassAnnotation(sootClass)) {
            if (controllerTag.contains(annotationTag.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Router parseToRouteMethod(SootMethod sootMethod) {
        return new Router(sootMethod.getDeclaringClass(), sootMethod, getMethodRoutePath(sootMethod));
    }

    private void loadSpringRouteConfigFromFile(Path file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SpringRouteConfig springRouteConfig = objectMapper.readValue(file.toFile(), SpringRouteConfig.class);

            controllerTag.addAll(springRouteConfig.getControllerClassTags());
            routeMethodTag.addAll(springRouteConfig.getRequestMethodTags());
            logger.info(String.format("load %d spring controller tag, %d spring request method tag",
                    springRouteConfig.getControllerClassTags().size(),
                    springRouteConfig.getControllerClassTags().size()));
        } catch (Exception e) {
            logger.warn(String.format("load spring route config from %s file fail: %s", file, e.getMessage()));
        }
    }

    public Set<String> getMethodRoutePath(SootMethod sootMethod) {
        Set<String> routeList = new HashSet<>();
        for (String routeAnnotation : routeMethodTag) {
            AnnotationTag annotationTag = TagUtil.searchAnnotation(TagUtil.getMethodAnnotation(sootMethod), routeAnnotation);
            if (annotationTag == null) {
                continue;
            }
            AnnotationElem annotationElem = TagUtil.getAnnotationElem(annotationTag, "value");
            if (annotationElem instanceof AnnotationArrayElem) {
                ((AnnotationArrayElem) annotationElem).getValues().forEach(annotationElem1 -> {
                    if (annotationElem1 instanceof AnnotationStringElem) {
                        routeList.add(((AnnotationStringElem) annotationElem1).getValue());
                    }
                });
            }
            return routeList;
        }
        return routeList;
    }

    static class SpringRouteConfig {
        @JsonProperty("controllerClassTags")
        private List<String> controllerClassTags;
        @JsonProperty("requestMethodTags")
        private List<String> requestMethodTags;

        public List<String> getControllerClassTags() {
            return controllerClassTags;
        }

        public void setControllerClassTags(List<String> controllerClassTags) {
            this.controllerClassTags = controllerClassTags;
        }

        public List<String> getRequestMethodTags() {
            return requestMethodTags;
        }

        public void setRequestMethodTags(List<String> requestMethodTags) {
            this.requestMethodTags = requestMethodTags;
        }
    }
}
