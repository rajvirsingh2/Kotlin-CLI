package com.rajvir.kotlin_cli.commands.prod

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.rajvir.kotlin_cli.storage.Store

// Parent command for productivity tools (like to-do list)
class ProdCommands : CliktCommand() {
    override fun run() = Unit
}

// Placeholder for main "todo" command group
class Todo : CliktCommand() {
    override fun run() = Unit
}

// Command to add items to the to-do list
class TodoAdd : CliktCommand(name = "add") {
    private val items by argument(help = "The to-do item text").multiple(required = true)

    override fun run() {
        // Get current list from store or use empty list
        val currentList = Store["todo_list"]?.split(";;") ?: emptyList()
        // Add the new item(s) (joined as one string)
        val newList = currentList + items.joinToString(" ")
        // Save updated list back to the store
        Store["todo_list"] = newList.joinToString(";;")
        echo("✅ Added to-do item.")
    }
}

// Command to display all to-do items
class TodoList : CliktCommand(name = "list") {
    override fun run() {
        // Get list from store or use empty
        val todoList = Store["todo_list"]?.split(";;") ?: emptyList()
        if (todoList.isEmpty()) {
            echo("To-do list is empty!")
        } else {
            // Print each item with its number
            todoList.forEachIndexed { index, item ->
                echo("${index + 1}. $item")
            }
        }
    }
}

// Command to mark items as done (remove them by index)
class TodoDone : CliktCommand(name = "done") {
    private val numbers by argument(help = "The numbers of the items to mark as done").multiple(required = true)

    override fun run() {
        // Get current list from store or return if none
        val todoList = Store["todo_list"]?.split(";;")?.toMutableList() ?: return
        // Convert input numbers to valid indices and sort descending to avoid shifting
        val indicesToRemove = numbers.mapNotNull { it.toIntOrNull()?.minus(1) }.sortedDescending()
        // Remove selected items
        indicesToRemove.forEach { if (it in todoList.indices) todoList.removeAt(it) }
        // Save updated list
        Store["todo_list"] = todoList.joinToString(";;")
        echo("✅ Updated to-do list.")
    }
}