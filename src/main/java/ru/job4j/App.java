package ru.job4j;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class App {
    private static ArgsName argsName;

    public static void main(String[] args) throws IOException {
        argsName = ArgsName.of(args);
        validateArguments(args);
        Predicate<Path> condition = getCondition();
        List<Path> sources = search(Paths.get(argsName.get("d")), condition);
        writeToFile(sources);
    }

    private static List<Path> search(Path root, Predicate<Path> condition) throws IOException {
        SearchFiles searcher = new SearchFiles(condition);
        Files.walkFileTree(root, searcher);
        return searcher.getPaths();
    }

    private static void writeToFile(List<Path> sources) {
        try (PrintWriter writer = new PrintWriter(argsName.get("o"))) {
            sources.forEach(path ->  writer.println(path.toFile().getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Predicate<Path> getCondition() {
        Predicate<Path> condition = null;
        String searchCondition = argsName.get("n");
        String searchType = argsName.get("t");
        if ("mask".equals(searchType)) {
            String pattern = "^"
                             + searchCondition
                                     .replace(".", "\\.")
                                     .replace("*", ".*")
                                     .replace("?", ".")
                             + "$";
            condition = path -> Pattern.compile(pattern).matcher(path.toFile().getName()).matches();
        } else if ("name".equals(searchType)) {
            condition = path -> path.toFile().getName().equals(searchCondition);
        } else if ("regex".equals(searchType)) {
            condition = path -> Pattern.compile(searchCondition).matcher(path.toFile().getName()).matches();
        }
        return condition;
    }

    private static void validateArguments(String[] args) {
        //TODO проверить валидацию, дополнить при необходимости
        List.of("d", "n", "t", "o").forEach(v -> argsName.get(v));
        if (args.length == 0) {
            throw new IllegalArgumentException("Параметры не переданы. Укажите условия поиска");
        }
        if (args.length < 4) {
            throw new IllegalArgumentException("Не все параметры указаны");
        }
        if (args.length > 4) {
            throw new IllegalArgumentException("Удалите лишние параметры");
        }
        File directory = new File(argsName.get("d"));
        if (!directory.exists()) {
            throw new IllegalArgumentException(format("Директория не существует: %s", directory.getAbsoluteFile()));
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(format("Это не директория: %s", directory.getAbsoluteFile()));
        }
    }
}
