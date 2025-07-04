package com.rajvir.kotlin_cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import java.awt.Desktop
import java.nio.file.Files
import java.nio.file.Path

class AppCommands: CliktCommand() {
    override fun run() {
        echo("Please specify an application operation: open.")
    }
}

class AppOpenCommand: CliktCommand() {
    private val appPath: Path by argument(help = "The path to the application/executable.").path()

    override fun run() {
        try {
            if (!Files.exists(appPath)) {
                echo("Error: Application path '$appPath' does not exist.", err = true)
                return
            }
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(appPath.toFile())
                    echo("Attempting to open application: '$appPath'")
                } else {
                    echo("Error: 'Desktop.Action.OPEN' is not supported on this platform.", err = true)
                }
            } else {
                echo("Error: Desktop API is not supported on this platform. Cannot open application.", err = true)
            }
        } catch (e: Exception) {
            echo("Error opening application '$appPath': ${e.message}", err = true)
            echo("Tip: Ensure the path is correct and the application is executable.", err = true)
        }
    }
}