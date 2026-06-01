package org.example.gui.controllers;

import org.example.gui.commands.Clear;
import org.example.gui.commands.Show;

public class ClearController {
    public static void onClearCLick() {
        new Clear().executeCommand();
    }
}
