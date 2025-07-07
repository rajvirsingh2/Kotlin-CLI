package com.rajvir.kotlin_cli.commands.`fun`

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument

class FunCommands : CliktCommand() {
    override fun run() = Unit
}

class Cowsay : CliktCommand() {
    private val message by argument(help = "The message for the cow to say")
    override fun run() {
        val top = " " + "_".repeat(message.length + 2)
        val mid = "< ${message} >"
        val bot = " " + "-".repeat(message.length + 2)
        val cow = """
              \   ^__^
               \  (oo)\_______
                  (__)\       )\/\
                      ||----w |
                      ||     ||
        """.trimIndent()
        echo(top)
        echo(mid)
        echo(bot)
        echo(cow)
    }
}
