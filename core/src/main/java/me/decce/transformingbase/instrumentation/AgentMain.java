package me.decce.transformingbase.instrumentation;

import java.lang.instrument.Instrumentation;

public class AgentMain {
    public static Instrumentation instrumentation;
    public static void agentmain(String args, Instrumentation instrumentation) {
        AgentMain.instrumentation = instrumentation;
    }
}
