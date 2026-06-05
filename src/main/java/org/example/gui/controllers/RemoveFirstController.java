package org.example.gui.controllers;

import org.example.gui.commands.RemoveFirst;

import static org.example.gui.Main.server;

/**
 * Контроллер кнопки удалить первый элемент
 */
public class RemoveFirstController {
    public static void onRemoveFirstClick() {
        new RemoveFirst().executeCommand(null, server, null);
    }
}
