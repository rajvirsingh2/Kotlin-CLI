package com.rajvir.kotlin_cli.commands.prod

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.rajvir.kotlin_cli.storage.Store

class ProdCommands:CliktCommand() {
    override fun run() = Unit
}

class Todo : CliktCommand() {
    override fun run() = Unit
}

class TodoAdd : CliktCommand(name = "add") {
    private val items by argument(help = "The to-do item text").multiple(required = true)
    override fun run() {
        val currentList = Store["todo_list"]?.split(";;") ?: emptyList()
        val newList = currentList + items.joinToString(" ")
        Store["todo_list"] = newList.joinToString(";;")
        echo("✅ Added to-do item.")
    }
}

class TodoList : CliktCommand(name = "list") {
    override fun run() {
        val todoList = Store["todo_list"]?.split(";;") ?: emptyList()
        if (todoList.isEmpty()) {
            echo("To-do list is empty!")
        } else {
            todoList.forEachIndexed { index, item ->
                echo("${index + 1}. $item")
            }
        }
    }
}

class TodoDone : CliktCommand(name = "done") {
    private val numbers by argument(help = "The numbers of the items to mark as done").multiple(required = true)
    override fun run() {
        val todoList = Store["todo_list"]?.split(";;")?.toMutableList() ?: return
        val indicesToRemove = numbers.mapNotNull { it.toIntOrNull()?.minus(1) }.sortedDescending()
        indicesToRemove.forEach { if (it in todoList.indices) todoList.removeAt(it) }
        Store["todo_list"] = todoList.joinToString(";;")
        echo("✅ Updated to-do list.")
    }
}
