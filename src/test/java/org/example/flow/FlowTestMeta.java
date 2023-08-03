package org.example.flow;

import java.util.List;

public class FlowTestMeta {
    private String skip;
    private String description;
    private List<String> entries;
    private List<Source> sources;
    private List<Sink> sinks;
    private Expect expect;

    public String getSkip() {
        return skip;
    }

    public void setSkip(String skip) {
        this.skip = skip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public List<Sink> getSinks() {
        return sinks;
    }

    public void setSinks(List<Sink> sinks) {
        this.sinks = sinks;
    }

    public Expect getExpect() {
        return expect;
    }

    public void setExpect(Expect expect) {
        this.expect = expect;
    }
}