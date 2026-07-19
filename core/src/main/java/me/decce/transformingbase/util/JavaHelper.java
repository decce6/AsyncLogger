package me.decce.transformingbase.util;

import java.io.OutputStream;

public class JavaHelper {
    public static OutputStream nullOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int b) {}
        };
    }
}
