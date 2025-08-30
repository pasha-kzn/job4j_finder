package ru.job4j;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class ArgsName {
    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        if (values.get(key) == null) {
            throw new IllegalArgumentException(format("This key: '%s' is missing", key));
        }
        return values.get(key);
    }

    private void parse(String[] args) {
        for (String arg : args) {
            String[] strings = arg.split("=", 2);
            values.put(strings[0].substring(1), strings[1]);
        }
    }

    public static ArgsName of(String[] args) {
        validate(args);
        ArgsName argsName = new ArgsName();
        argsName.parse(args);
        return argsName;
    }

    private static void validate(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments not passed to program");
        }
        for (String arg : args) {
            if (!arg.contains("=")) {
                throw new IllegalArgumentException(format("Error: This argument '%s' does not contain an equal sign", arg));
            }
            if (!arg.startsWith("-")) {
                throw new IllegalArgumentException(format("Error: This argument '%s' does not start with a '-' character", arg));
            }
            if (arg.startsWith("-=")) {
                throw new IllegalArgumentException(format("Error: This argument '%s' does not contain a key", arg));
            }
            String[] strings = arg.split("=", 2);
            if (strings[1].isEmpty()) {
                throw new IllegalArgumentException(format("Error: This argument '%s' does not contain a value", arg));
            }
        }
    }
}