package com.rajvir.kotlin_cli

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.rajvir.kotlin_cli.commands.ai.AiCommands
import com.rajvir.kotlin_cli.commands.ai.Ask
import com.rajvir.kotlin_cli.commands.dev.*
import com.rajvir.kotlin_cli.commands.fs.*
import com.rajvir.kotlin_cli.commands.`fun`.Cowsay
import com.rajvir.kotlin_cli.commands.`fun`.FunCommands
import com.rajvir.kotlin_cli.commands.kv.*
import com.rajvir.kotlin_cli.commands.net.*
import com.rajvir.kotlin_cli.commands.prod.*
import com.rajvir.kotlin_cli.commands.sec.JwtDecode
import com.rajvir.kotlin_cli.commands.sec.PassGen
import com.rajvir.kotlin_cli.commands.sec.SecurityCommands

// Main CLI class that extends CliktCommand
class Kv: CliktCommand() {

    init {
        // Adds --version option with version info
        versionOption(version = "1.0.0")

        // Sets up shared context object
        context {
            obj = AppContext()
        }
    }

    // Loads configuration settings
    private val config = ConfigLoader.load()

    // Defines --verbose flag for detailed output
    private val verbose by option("-v", "--verbose", help = "Enable verbose output.").flag(
        default = (config.defaultLog == "DEBUG")
    )

    // Main logic that runs when this command is executed
    override fun run() {
        // Access shared context
        val context = currentContext.obj as AppContext

        // Set verbose flag in context
        context.verbose = this.verbose

        // Print message if verbose is enabled
        if (context.verbose) {
            echo("Verbose mode is enabled.")
        }

        // Print current working directory
        echo("Current directory: ${currentDirectory.absolutePath}")
    }
}

// Entry point of the CLI application
fun main(args: Array<String>) {
    Kv().subcommands(
        // K-V (key-value) related commands
        Set(), Get(), List(),

        // File system commands
        FsCommands().subcommands(
            CreateFile(), DeleteFile(), WriteFile(), ReadFile(), Cd(), Tree(), CheckSum(), Search(), Clean()
        ),

        // Developer tools commands
        DevCommands().subcommands(
            Server(), RunCode(), Format(), Git().subcommands(GitLog(), GitBranches())
        ),

        // Network-related commands
        NetCommands().subcommands(
            Ping(), Scan(), Request(), Download()
        ),

        // AI-related commands
        AiCommands().subcommands(
            Ask()
        ),

        // Security-related commands
        SecurityCommands().subcommands(
            PassGen(), JwtDecode()
        ),

        // Fun/entertainment commands
        FunCommands().subcommands(
            Cowsay()
        ),

        // Productivity-related commands
        ProdCommands().subcommands(
            Todo(), TodoAdd(), TodoList(), TodoDone()
        )
    ).main(args) // Start CLI with passed arguments
}
