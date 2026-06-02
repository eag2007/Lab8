package org.example.gui.controllers;

import org.example.gui.commands.Show;
import org.example.gui.commands.Subscribe;

import static org.example.gui.Main.server;

public class SubscribeController {
    public static void onSubscribe() {
        new Subscribe().executeCommand(null, server, null);
    }

    public static void execute() {
        new Show().executeCommand(null, server, null);
    }
}
