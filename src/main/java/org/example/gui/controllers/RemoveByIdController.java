package org.example.gui.controllers;

import org.example.gui.commands.RemoveById;

import static org.example.gui.Main.server;

/**
 * Контроллер кнопки удаления по id
 */
public class RemoveByIdController {
    public static void onRemoveByIdControllerClick(long id) {
        new RemoveById().executeCommand(new String[]{String.valueOf(id)}, server, null);
    }
}
