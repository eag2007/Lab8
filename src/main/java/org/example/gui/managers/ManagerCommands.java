package org.example.gui.managers;

import org.example.gui.commands.*;
import org.example.gui.interfaces.Command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ManagerCommands {
    private static final Map<String, Command> commands = new HashMap<>();
    private static final LinkedList<String> history = new LinkedList<>();

    static {
        commands.put("add", new Add());
        commands.put("add_if_max", new AddIfMax());
        commands.put("clear", new Clear());
        commands.put("info", new Info());
        commands.put("show", new Show());
        commands.put("update", new Update());
        commands.put("remove_by_id", new RemoveById());
        commands.put("remove_first", new RemoveFirst());
        commands.put("remove_all_by_distance", new RemoveAllByDistance());
        commands.put("average_of_distance", new AverageOfDistance());
        commands.put("filter_less_than_distance", new FilterLessThanDistance());
        commands.put("subscribe", new Subscribe());
        commands.put("logout", new Logout());
        commands.put("help", new Help());
        commands.put("history", new History());
    }

    public static Command get(String name) {
        return commands.get(name);
    }

    public static void addToHistory(String cmd) {
        if (cmd == null || cmd.isBlank()) return;
        history.add(cmd);
        if (history.size() > 14) {
            history.removeFirst();
        }
    }

    public static String getHistory() {
        if (history.isEmpty()) {
            return "История пуста";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(i + 1).append(". ").append(history.get(i)).append("\n");
        }
        return sb.toString();
    }

    public static String getHelp() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Command> e : commands.entrySet()) {
            sb.append(e.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}