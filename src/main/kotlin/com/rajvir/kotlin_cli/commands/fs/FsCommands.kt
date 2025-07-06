package com.rajvir.kotlin_cli.commands.fs

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.long
import com.rajvir.kotlin_cli.currentDirectory
import com.rajvir.kotlin_cli.storage.Store
import java.io.File
import java.math.BigInteger
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.security.MessageDigest
import kotlin.io.path.getLastModifiedTime

//Here File-System manipulation commands are specified

class FsCommands: CliktCommand(name = "fs"){
    //Nothing to execute at top level
    override fun run() = Unit
}

//Command to create the file in the current directory
class CreateFile:CliktCommand(name = "create-file"){
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

//Command to delete the specified file
class DeleteFile : CliktCommand(name = "delete-file"){
    private val filename by argument(help = "The name of the file to delete")
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

//Command to write to the file
class WriteFile : CliktCommand(name = "write"){
    private val filename by argument(help = "The name of the file to write to")
    private val content by argument(help = "The text content to write into the file")
    override fun run() {
        val file = File(currentDirectory, filename)
        file.writeText(content)
        echo("✅ Wrote to file: ${file.name}")
    }
}

//Command to read the contents of the file
class ReadFile : CliktCommand(name = "read"){
    private val filename by argument(help = "The name of the file to read")
    override fun run() {
        val file = File(currentDirectory, filename)
        if(file.exists()){
            echo(file.readText())
        }else{
            echo("❌ Error: File '${file.name}' not found.", err = true)
        }
    }
}

//Command to change the directory
class Cd : CliktCommand(name = "cd") {
    //Destination path
    private val path by argument(help = "Directory to move to")

    override fun run() {
        val target = File(currentDirectory, path).canonicalFile
        if (target.isDirectory) {
            currentDirectory = target
            Store.setCurrentDir(target)  //persistence
            echo("Current directory changed to: ${target.absolutePath}")
        } else {
            echo("❌ Error: '${target.absolutePath}' is not a valid directory.", err = true)
            currentContext.exitProcess(1)
        }
    }
}

//Pretty-print directory structure
class Tree: CliktCommand(){
    //Max depth to display
    private val maxDepth by option("-d", "--depth", help = "Max depth to display").long().default(3)
    override fun run() {
        echo(currentDirectory.path)
        printTree(currentDirectory, "", true, 0)
    }

    //Recursive function to print the tree
    private fun printTree(dir: File, prefix: String, isLast: Boolean, depth: Int){
        if(depth >= maxDepth) return
        val children = dir.listFiles()?.sortedBy { it.name }?:return
        children.forEachIndexed { index, file ->
            val newPref = prefix+if(isLast)"   " else "|  "
            val connector = if (index == children.lastIndex) "└── " else "├── "
            echo("$prefix$connector${file.name}")
            if(file.isDirectory) printTree(file, newPref, index == children.lastIndex, depth+1)
        }
    }
}

//Compute cryptographic digest of a file
class CheckSum: CliktCommand(){
    //Supported algorithms
    @Suppress("unused")
    enum class Algo{MD5, SHA1, SHA256, SHA512}
    private val algo by option(help = "Checksum algorithm").enum<Algo>(ignoreCase = true).default(Algo.MD5)
    private val fileName by argument(help = "File to checksum")

    override fun run() {
        val file = File(currentDirectory, fileName)
        if(!file.exists() || !file.isFile) return echo("❌ Error: File not found at '${file.path}'", err = true)
        //Compute digest
        val digest = MessageDigest.getInstance(algo.name)
        //Stream file in 8 KiB chunks
        file.inputStream().use { fis->
            val buffer = ByteArray(8192)
            var n: Int
            while(fis.read(buffer).also{ n=it } != -1){
                digest.update(buffer, 0, n)
            }
        }
        //Convert digest to hex string
        val hex = BigInteger(1, digest.digest()).toString(16).padStart(32, '0')
        echo("$hex $fileName")
    }
}

//Walkthrough the directory and apply multiple filters
class Search: CliktCommand(){
    private val namePattern by option("-n", "--name", help = "Pattern for file name (e.g. '*.txt')").default("*")
    private val minSize by option("-s", "--size-greater-than", help = "Min file size in bytes").long()
    private val modifiedSince by option("-m", "--modified-since", help = "Find files modified after date (YYYY-MM-DD)")

    override fun run() {
        val pathMatch = FileSystems.getDefault().getPathMatcher("glob: $namePattern")
        val searchTime = modifiedSince?.let { Paths.get(it).toFile().toPath().getLastModifiedTime().toMillis() }
        val res = currentDirectory.walk()
            .filter{ it.isFile }
            .filter { pathMatch.matches(it.toPath().fileName) }
            .filter { minSize==null || it.length()>=minSize!! }
            .filter { searchTime==null || it.lastModified()>=searchTime }
            .toList()

        if(res.isEmpty()) echo("No files found") else res.forEach{ echo(it.relativeTo(currentDirectory).path) }
    }
}

//Delete temporary or log files matching one or more glob patterns.
class Clean: CliktCommand(){
    private val patterns by argument(help = "Glob patterns of the file to delete").multiple(required = true)
    private val dryRun by option("--dry-run", help = "Show which files would be deleted").flag()

    override fun run() {
        val matchers = patterns.map {
            FileSystems.getDefault().getPathMatcher("glob:$it")
        }

        val file = currentDirectory.walk().filter { file->
            matchers.any{ matcher->
                matcher.matches(file.toPath().fileName)
            }
        }.toList()

        if(file.isEmpty()) {
            echo("No files found")
            return
        }

        echo("The following files will be deleted:")
        file.forEach{
            echo("-${it.relativeTo(currentDirectory).path}")
        }
        if(dryRun){
            echo("\nDry run complete. Nothing deleted")
            return
        }
        if ("Are you sure you want to delete these files?".confirmManual()) {
            var deletedCount = 0
            file.forEach { if (it.delete()) deletedCount++ }
            echo("✅ Successfully deleted $deletedCount files.")
        }else{
            echo("Operation Cancelled")
        }
    }

    private fun String.confirmManual(): Boolean {
        print("$this [y/N]: ")
        return readlnOrNull()?.trim()?.lowercase() == "y"
    }
}