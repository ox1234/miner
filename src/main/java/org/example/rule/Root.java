package org.example.rule;

import java.util.ArrayList;

public    class Root{
    public String language;
    public boolean useFlow;
    public boolean dependency;
    public int dependencyDeep;
    public boolean single;
    public Filter filter;
    public WhiteList whiteList;
    public ArrayList<Rule> rules;
}