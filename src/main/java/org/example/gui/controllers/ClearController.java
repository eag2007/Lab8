package org.example.gui.controllers;

import org.example.gui.commands.Clear;
import org.example.gui.commands.Show;

import static org.example.gui.Main.server;

public class ClearController {
    public static void onClearCLick() {
        new Clear().executeCommand(null, server, null);
    }
}
