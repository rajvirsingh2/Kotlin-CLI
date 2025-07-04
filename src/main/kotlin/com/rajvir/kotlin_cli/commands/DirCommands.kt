package com.rajvir.kotlin_cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class DirCommands: CliktCommand() {
    override fun run() {
        echo("Please specify a directory operation: create or delete.")
    }
}

class DirCreateCommand: CliktCommand() {
    private val dirPath: Path by argument(help = "The path of the directory to create.").path()

    override fun run() {
        try {
            if (Files.exists(dirPath)) {
                echo("Error: Directory '$dirPath' already exists.", err = true)
            } else {
                Files.createDirectories(dirPath) // createDirectories handles parent directories
                echo("Directory '$dirPath' created successfully.")
            }
        } catch (e: Exception) {
            echo("Error creating directory '$dirPath': ${e.message}", err = true)
        }
    }
}

class DirDeleteCommand: CliktCommand() {
    private val dirPath: Path by argument(help = "The path of the directory to delete.").path()
    private val recursive: Boolean by option("-r", "--recursive", help = "Delete directory and its contents recursively.").flag()

    override fun run() {
        try {
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                echo("Error: Directory '$dirPath' does not exist or is not a directory.", err = true)
                return
            }

            if (recursive) {
                Files.walk(dirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete)
                echo("Directory '$dirPath' and its contents deleted recursively.")
            } else {
                Files.delete(dirPath) // Will only delete if empty
                echo("Directory '$dirPath' deleted successfully (only if empty).")
            }
        } catch (e: Exception) {
            echo("Error deleting directory '$dirPath': ${e.message}", err = true)
        }
    }
}