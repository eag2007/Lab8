package org.example.gui.controllers;

import org.example.gui.commands.Show;
import org.example.gui.commands.Subscribe;

public class SubscribeController {
    public static void onSubscribe() {
        new Subscribe().executeCommand();
    }

    public static void execute() {
        new Show().executeCommand();
    }
}
