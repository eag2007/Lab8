package org.example.gui.commands;

import org.example.gui.Main;
import org.example.gui.managers.ManagerAuth;
import org.example.gui.managers.ManagerCommands;
import org.example.gui.managers.ManagerValidation;
import org.example.packet.CommandPacket;
import org.example.packet.collection.RouteClient;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Класс исполнения скрипта execute_script
 * Класс считывает содержимое файла и построчно по команде отправляет всё на сервер и отрисовывает ответ
 * Ошибки собираются в список и показываются одним окном
 */
public class ExecuteScript {

    private static final int FIELDS = 11;
    private static final Set<String> active = new HashSet<>();

    public static List<String> run(File file) {
        List<String> errors = new ArrayList<>();
        execute(file, errors);
        return errors;
    }

    private static void execute(File file, List<String> errors) {
        ManagerCommands.addToHistory("execute_script");
        if (file == null || !file.exists()) {
            errors.add("Файл скрипта не найден: " + (file == null ? "?" : file.getPath()));
            return;
        }
        String path = file.getAbsolutePath();
        if (!active.add(path)) {
            errors.add("Рекурсия в скрипте: " + file.getName());
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            ManagerValidation validator = new ManagerValidation();

            int i = 0;
            while (i < lines.size()) {
                int lineNo = i + 1;
                String line = lines.get(i++).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.replaceAll("\\s+", " ").split(" ");
                String cmd = parts[0];
                String[] args = Arrays.copyOfRange(parts, 1, parts.length);

                switch (cmd) {
                    case "add", "add_if_max", "update" -> {
                        String[] sendArgs = null;
                        if (cmd.equals("update")) {
                            if (args.length == 0) {
                                errors.add("Строка " + lineNo + ": update требует id");
                                i += FIELDS;
                                break;
                            }
                            sendArgs = new String[]{args[0]};
                        }
                        List<String> values = nextLines(lines, i);
                        i += FIELDS;
                        try {
                            RouteClient route = validator.validateFromScript(values);
                            if (!Main.sendQuiet(new CommandPacket(cmd, sendArgs, route,
                                    ManagerAuth.getLogin(), ManagerAuth.getPassword()))) {
                                errors.add("Выполнение остановлено: сервер недоступен");
                                return;
                            }
                        } catch (IllegalArgumentException e) {
                            errors.add("Строка " + lineNo + " (" + cmd + "): " + e.getMessage());
                        }
                    }
                    case "execute_script" -> {
                        if (args.length == 0) {
                            errors.add("Строка " + lineNo + ": execute_script требует имя файла");
                            break;
                        }
                        execute(resolve(args[0], file.getParentFile()), errors);
                    }
                    case "show", "ls", "info", "clear", "remove_first", "average_of_distance",
                         "remove_by_id", "remove_all_by_distance", "filter_less_than_distance", "subscribe" -> {
                        String type = cmd.equals("ls") ? "show" : cmd;
                        if (!Main.sendQuiet(new CommandPacket(type, args, null,
                                ManagerAuth.getLogin(), ManagerAuth.getPassword()))) {
                            errors.add("Выполнение остановлено: сервер недоступен");
                            return;
                        }
                    }
                    default -> errors.add("Строка " + lineNo + ": неизвестная команда '" + cmd + "'");
                }
            }
        } catch (IOException e) {
            errors.add("Ошибка чтения файла '" + file.getName() + "': " + e.getMessage());
        } finally {
            active.remove(path);
        }
    }

    /**
    * Считывает 11 строк полей
    */
    private static List<String> nextLines(List<String> lines, int from) {
        List<String> values = new ArrayList<>(FIELDS);
        for (int k = 0; k < FIELDS; k++) {
            int idx = from + k;
            values.add(idx < lines.size() ? lines.get(idx) : null);
        }
        return values;
    }

    private static File resolve(String name, File parentDir) {
        File f = new File(name);
        if (f.exists()) return f;
        if (parentDir != null) {
            File rel = new File(parentDir, name);
            if (rel.exists()) return rel;
        }
        return new File("src/main/java/org/example/" + name);
    }
}