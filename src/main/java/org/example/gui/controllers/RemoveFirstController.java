package org.example.gui.controllers;

import org.example.gui.commands.RemoveFirst;

public class RemoveFirstController {
    public static void onRemoveFirstClick() {
        new RemoveFirst().executeCommand();
    }
}
