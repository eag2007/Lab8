package org.example.gui.controllers;

import org.example.gui.commands.Subscribe;

import static org.example.gui.Main.server;

public class SubscribeController {
    public static void onSubscribe() {
        new Subscribe().executeCommand(null, server, null);
    }
}
