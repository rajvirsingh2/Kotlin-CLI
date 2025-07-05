package com.rajvir.kotlin_cli

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.rajvir.kotlin_cli.commands.fs.*
import com.rajvir.kotlin_cli.commands.kv.*

class Kv:CliktCommand(){

    init{
        // This automatically adds the --version flag.
        // It reads the version from the build.gradle.kts file.
        versionOption(version = "1.0.0")
        context {
            obj = AppContext()
        }
    }

    private val config = ConfigLoader.load()

    private val verbose by option("-v", "--verbose", help = "Enable verbose output.").flag(
        default = (config.defaultLog == "DEBUG")
    )

    override fun run() {
        val context = currentContext.obj as AppContext
        context.verbose = this.verbose
        if (context.verbose) {
            echo("Verbose mode is enabled.")
        }
        echo("Current directory: ${currentDirectory.absolutePath}")
    }
}

fun main(args: Array<String>){
    Kv().subcommands(
        //K-V commands
        Set(), Get(), List(),
        //File manipulation commands
        CreateFile(), DeleteFile(), WriteFile(), ReadFile(), Cd()
    ).main(args)
}