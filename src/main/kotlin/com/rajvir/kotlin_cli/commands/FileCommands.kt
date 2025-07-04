package com.rajvir.kotlin_cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class FileCommands: CliktCommand() {
    override fun run() {
        echo("Specify the file operation: create, delete or copy")
    }
}

class FileCreateCommand: CliktCommand(){
    private val filePath: Path by argument(help = "Path of the file to create").path()

    override fun run() {
        try{
            if(Files.exists(filePath)){
                echo("Error: File '$filePath' already exists")
            }else{
                Files.createFile(filePath)
                echo("Created new file")
            }
        }catch (e:Exception){
            echo("Error creating file: ${e.message}", err = true)
        }
    }
}

class FileDeleteCommand: CliktCommand() {
    private val filePath: Path by argument(help = "Path of the file to delete").path()

    override fun run() {
        try {
            if(Files.exists(filePath) && Files.isRegularFile(filePath)){
                Files.delete(filePath)
                echo("Deleted file")
            }else{
                echo("Error: File '$filePath' does not exist or is not a regular file.", err = true)
            }
        }catch (e:Exception){
            echo("Error deleting file: ${e.message}", err = true)
        }
    }
}

class FileCopyCommand: CliktCommand() {
    private val sourcePath: Path by argument(name = "SOURCE", help = "Path of the source file").path()
    private val destinationPath: Path by argument(name = "DESTINATION", help = "Path of the destination file").path()
    private val overwrite: Boolean by option("-o", "--overwrite", help = "Overwrite existing file").flag()

    override fun run() {
        try{
            if(!Files.exists(sourcePath) || !Files.isRegularFile(sourcePath)){
                echo("Error: Source file '$sourcePath' does not exist or is not a regular file.", err = true)
                return
            }
            if(Files.exists(destinationPath) && !overwrite){
                echo("Error: Destination file '$destinationPath' already exists. Use -o to overwrite.", err = true)
                return
            }
            val options = if (overwrite) arrayOf(StandardCopyOption.REPLACE_EXISTING) else arrayOf()
            Files.copy(sourcePath, destinationPath, *options)
            echo("File '$sourcePath' copied to '$destinationPath' successfully.")
        }catch (e:Exception){
            echo("Error copying file from '$sourcePath' to '$destinationPath': ${e.message}", err = true)
        }
    }
}