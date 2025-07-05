package com.rajvir.kotlin_cli.commands.fs

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.rajvir.kotlin_cli.currentDirectory
import java.io.File

//Here File-System manipulation commands are specified

class CreateFile:CliktCommand(name = "create file"){
    private val fileName by argument(help = "Name of the file to create")

    override fun run() {
        val file = File(currentDirectory, fileName)
        if(file.exists()){
            echo("❌ Error: File '${file.name}' already exists.\", err = true")
        }else{
            file.createNewFile()
            echo("✅ Created file: ${file.name}")
        }
    }
}


class DeleteFile : CliktCommand(name = "delete file"){
    val filename by argument(help = "The name of the file to delete")
    override fun run() {
        val file = File(currentDirectory, filename)
        if(file.exists()){
            if(file.delete()){
                echo("✅ Deleted file: ${file.name}")
            }else{
                echo("❌ Error: Could not delete file '${file.name}'.", err = true)
            }
        }else{
            echo("❌ Error: File '${file.name}' not found.", err = true)
        }
    }
}

class WriteFile : CliktCommand(name = "write"){
    val filename by argument(help = "The name of the file to write to")
    val content by argument(help = "The text content to write into the file")
    override fun run() {
        val file = File(currentDirectory, filename)
        file.writeText(content)
        echo("✅ Wrote to file: ${file.name}")
    }
}

class ReadFile : CliktCommand(name = "read"){
    val filename by argument(help = "The name of the file to read")
    override fun run() {
        val file = File(currentDirectory, filename)
        if(file.exists()){
            echo(file.readText())
        }else{
            echo("❌ Error: File '${file.name}' not found.", err = true)
        }
    }
}

class Cd : CliktCommand(){
    val path by argument(help = "The directory to move to (e.g., '..', 'subdir', '/home/user')")
    override fun run() {
        val newDir = File(currentDirectory, path).canonicalFile
        if(newDir.isDirectory){
            currentDirectory = newDir
            echo("Current directory changed to: ${currentDirectory.absolutePath}")
        }else{
            echo("❌ Error: '${newDir.absolutePath}' is not a valid directory.", err = true)
        }
    }
}