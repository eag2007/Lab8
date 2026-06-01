package org.example.gui.controllers;

import org.example.gui.commands.RemoveById;

public class RemoveByIdController {
    public static void onRemoveByIdControllerClick(long id) {
        new RemoveById().executeCommand(new String[]{String.valueOf(id)});
    }
}
