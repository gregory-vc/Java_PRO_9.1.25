package org.example.csv;
import org.apache.commons.csv.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.*;

public class Csv {
    final private Method m;
    final private List<String> parsed;

    private static List<String> parseCsvRow(String row) throws IOException {
        CSVFormat fmt = CSVFormat.DEFAULT.builder()
                .setTrim(true)
                .setIgnoreSurroundingSpaces(true)
                .setQuote('"')
                .build();

        try (CSVParser parser = CSVParser.parse(new StringReader(row), fmt)) {
            CSVRecord rec = parser.iterator().next();
            List<String> cells = new ArrayList<>(rec.size());
            rec.forEach(cells::add);
            return cells;
        }
    }

    private static Object convert(String v, Class<?> t) {
        if (t == String.class) return v;
        if (t == int.class || t == Integer.class) return Integer.parseInt(v);
        if (t == long.class || t == Long.class)   return Long.parseLong(v);
        if (t == boolean.class || t == Boolean.class) return Boolean.parseBoolean(v);
        if (t == double.class || t == Double.class) return Double.parseDouble(v);
        if (t == float.class || t == Float.class) return Float.parseFloat(v);
        throw new IllegalArgumentException("Не умею конвертировать в " + t.getName());
    }

    private Object[] bindArgs() {
        Class<?>[] ts = this.m.getParameterTypes();
        if (ts.length != this.parsed.size())
            throw new IllegalArgumentException("Ожидалось " + ts.length + " значений, а получено " + this.parsed.size());
        Object[] args = new Object[ts.length];
        for (int i = 0; i < ts.length; i++) {
            args[i] = convert(this.parsed.get(i), ts[i]);
        }
        return args;
    }

    public Csv(Method m, String s) {
        try {
            this.m = m;
            this.parsed = parseCsvRow(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] GetArgs() {
        return bindArgs();
    }
}
