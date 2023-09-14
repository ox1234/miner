package org.example.config.sourcesink;

import java.util.List;
import java.util.Objects;

public class Sink {
    private String funcSig;
    private String sinkType;
    private List<Integer> sinkIdx;

    public Sink(String funcSig, String sinkType, List<Integer> sinkIdx) {
        this.funcSig = funcSig;
        this.sinkType = sinkType;
        this.sinkIdx = sinkIdx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sink sink = (Sink) o;
        return Objects.equals(funcSig, sink.funcSig) && Objects.equals(sinkType, sink.sinkType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcSig, sinkType);
    }

    public String getFuncSig() {
        return funcSig;
    }

    public void setFuncSig(String funcSig) {
        this.funcSig = funcSig;
    }

    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

    public List<Integer> getSinkIdx() {
        return sinkIdx;
    }

    public void setSinkIdx(List<Integer> sinkIdx) {
        this.sinkIdx = sinkIdx;
    }
}
