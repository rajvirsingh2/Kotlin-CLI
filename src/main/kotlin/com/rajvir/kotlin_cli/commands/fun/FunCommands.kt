package com.rajvir.kotlin_cli.commands.`fun`

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument

// Group command for fun-related features
class FunCommands : CliktCommand() {
    // No operation on base command
    override fun run() = Unit
}

// Command to display a fun "cowsay" message
class Cowsay : CliktCommand() {
    // Message to be spoken by the cow
    private val message by argument(help = "The message for the cow to say")

    override fun run() {
        // Top border of the speech bubble
        val top = " " + "_".repeat(message.length + 2)
        // Middle line with message in angled brackets
        val mid = "< ${message} >"
        // Bottom border of the speech bubble
        val bot = " " + "-".repeat(message.length + 2)

        // ASCII art of the cow
        val cow = """
              \   ^__^
               \  (oo)\_______
                  (__)\       )\/\
                      ||----w |
                      ||     ||
        """.trimIndent()

        // Print the message and the cow
        echo(top)
        echo(mid)
        echo(bot)
        echo(cow)
    }
}
