package me.decce.transformingbase.core;

import com.github.bsideup.jabel.Desugar;
import org.apache.logging.log4j.Level;

import java.util.regex.Pattern;

@Desugar
public record FilteringInfo(Level[] levels, String[] loggers, String[] strings, Pattern[] regexes) {
}
