package org.example.config.sourcesink;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.sourcesink.rule.SinkRule;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SourceSinkManager {
    private final Logger logger = LogManager.getLogger(SourceSinkManager.class);

    private Set<Source> sources;
    private Set<Sink> sinks;
    private Map<String, Sink> registeredSinkSigs;

    public SourceSinkManager() {
        this.sources = new HashSet<>();
        this.sinks = new HashSet<>();
        this.registeredSinkSigs = new HashMap<>();
    }

    public void addSink(Sink sink) {
        registeredSinkSigs.put(sink.getFuncSig(), sink);
        sinks.add(sink);
    }

    public void addSource(Source source) {
        sources.add(source);
    }

    public boolean isSinkSig(String sig) {
        return registeredSinkSigs.containsKey(sig);
    }

    public Sink getSink(String sig) {
        return registeredSinkSigs.get(sig);
    }

    public void loadSinkRuleFromFile(Path file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<SinkRule>> typeRef = new TypeReference<>() {
            };
            List<SinkRule> sinkRules = objectMapper.readValue(file.toFile(), typeRef);
            AtomicInteger count = new AtomicInteger();
            sinkRules.forEach(sinkRule -> {
                String sinkType = sinkRule.name;
                sinkRule.sinks.forEach(sink -> {
                    addSink(new Sink(sink.expression, sinkType, sink.index));
                    count.getAndIncrement();
                });
            });
            logger.info(String.format("load %d sink from %s file", count.get(), file.toAbsolutePath()));
        } catch (Exception e) {
            logger.warn(String.format("load sink rule from %s file fail: %s", file.toAbsolutePath(), e.getMessage()));
        }
    }

    private void loadSourceRuleFromFile(Path file) {
    }
}
