package me.decce.transformingbase.core.sysout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class RedirectingPrintStream extends WrappedPrintStream {
    private final Logger LOGGER;
    private final StringBuffer buffer;
    private final AtomicInteger buffering;

    public RedirectingPrintStream(String name, OutputStream out) {
        super(out);
        this.LOGGER = LogManager.getLogger(name);
        this.buffer = new StringBuffer();
        this.buffering = new AtomicInteger();
    }

    private void wrappedPrint(String s) {
        if (this.buffering.get() > 0) {
            this.buffer.append(s);
            return;
        }
        LOGGER.info(s);
    }

    @Override
    public void print(String s) {
        wrappedPrint(String.valueOf(s));
    }

    @Override
    public void println() {
        // No-op
    }

    @Override
    public void println(String x) {
        this.print(x);
        this.println();
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        buffering.getAndIncrement();
        var result = super.format(l, format, args);
        buffering.getAndDecrement();
        flushBuffer();
        return result;
    }

    @Override
    public PrintStream format(String format, Object... args) {
        buffering.getAndIncrement();
        var result = super.format(format, args);
        buffering.getAndDecrement();
        flushBuffer();
        return result;
    }

    private void flushBuffer() {
        if (buffering.get() == 0 && buffer.length() > 0) {
            print(buffer.toString());
            buffer.setLength(0);
        }
    }
}
