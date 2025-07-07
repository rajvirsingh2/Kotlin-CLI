package com.rajvir.kotlin_cli.commands.dev

import com.charleskorn.kaml.Yaml
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.rajvir.kotlin_cli.currentDirectory
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import kotlin.io.path.createTempDirectory

class DevCommands: CliktCommand(name = "dev"){
    override fun run()=Unit
}

class Server: CliktCommand(){
    private val port by option("-p","--port", help = "Port to bind the server to.").int().default(8000)

    override fun run() {
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext("/") { http->
            val file = File(currentDirectory, http.requestURI.path.drop(1)).canonicalFile
            http.sendResponseHeaders(200, 0)
            http.responseBody.use { os->
                if(file.startsWith(currentDirectory) && file.exists()) file.inputStream().use { it.copyTo(os) } else{
                    val notFound = "<h1>404 Not Found</h1>".toByteArray()
                    http.sendResponseHeaders(404,notFound.size.toLong())
                    os.write(notFound)
                }
            }
        }
        server.start()
        echo("✅ Server started on http://localhost:$port")
        echo("Press Ctrl+C to stop.")
        Thread.currentThread().join() //Keeps server running
    }
}

class RunCode : CliktCommand(name = "run-code") {
    private val language by argument(help = "Language of the snippet (js,kt,java,c,cpp,go)")
    private val inlineCode by argument(help = "Inline code snippet").optional()
    private val sourceFile by option("-f", "--file", help = "Path to a source file").file()

    override fun run() {
        val code = when {
            sourceFile != null && inlineCode != null -> {
                echo("❌ Provide code inline *or* via --file, not both.", err = true)
                return
            }
            sourceFile != null -> {
                if (!sourceFile!!.exists()) {
                    echo("❌ File '${sourceFile!!.absolutePath}' not found.", err = true)
                    return
                }
                sourceFile!!.readText()
            }
            inlineCode != null -> inlineCode!!
            else -> {
                echo("❌ Provide code inline or with --file.", err = true)
                return
            }
        }

        val tempDir = createTempDirectory("kotlin-cli-run").toFile()
        try {
            when (language.lowercase()) {
                "js" -> execute(listOf("node", "-e", code), tempDir)

                "kt" -> {
                    val src = File(tempDir, "script.kt").apply { writeText(code) }
                    val jar = File(tempDir, "script.jar")
                    execute(listOf("kotlinc", src.absolutePath, "-include-runtime", "-d", jar.absolutePath), tempDir) {
                        execute(listOf("java", "-jar", jar.absolutePath), tempDir)
                    }
                }

                "java" -> {
                    val src = File(tempDir, "Main.java").apply { writeText(code) }
                    execute(listOf("javac", src.absolutePath), tempDir) {
                        execute(listOf("java", "-cp", tempDir.absolutePath, "Main"), tempDir)
                    }
                }

                "c" -> {
                    val src = File(tempDir, "program.c").apply { writeText(code) }
                    val out = File(tempDir, "program.out")
                    execute(listOf("gcc", src.absolutePath, "-o", out.absolutePath), tempDir) {
                        execute(listOf(out.absolutePath), tempDir)
                    }
                }

                "cpp" -> {
                    val src = File(tempDir, "program.cpp").apply { writeText(code) }
                    val out = File(tempDir, "program.out")
                    execute(listOf("g++", src.absolutePath, "-o", out.absolutePath), tempDir) {
                        execute(listOf(out.absolutePath), tempDir)
                    }
                }

                "go" -> {
                    val src = File(tempDir, "main.go").apply { writeText(code) }
                    execute(listOf("go", "run", src.absolutePath), tempDir)
                }

                else -> echo("❌ Error: Unsupported language '$language'.", err = true)
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    private fun execute(command: List<String>, workDir: File, onSuccess: (() -> Unit)? = null) {
        try {
            val process = ProcessBuilder(command)
                .directory(workDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()
            process.waitFor(30, TimeUnit.SECONDS)

            if (process.exitValue() != 0) {
                echo("❌ Execution failed:", err = true)
                echo(error, err = true)
            } else {
                if (output.isNotBlank()) echo(output)
                onSuccess?.invoke()
            }
        } catch (e: IOException) {
            echo("❌ Error: Command '${command.first()}' not found. Is it installed and in your PATH?", err = true)
        }
    }
}


class Format: CliktCommand(){
    private val file by argument(help = "Path to JSON or YAML file.")
    override fun run() {
        val targetFile = File(currentDirectory,file)
        if(!targetFile.exists()) return echo("❌ Error: File not found at '${targetFile.path}'", err = true)
        val content = targetFile.readText()
        try{
            val formatted = when(targetFile.extension.lowercase()){
                "json" -> {
                    val json = Json{ prettyPrint=true }
                    val jsonObject = json.decodeFromString<JsonElement>(content)
                    json.encodeToString(JsonElement.serializer(), jsonObject)
                }
                "yml" , "yaml" ->{
                    val ymlObject = Yaml.default.parseToYamlNode(content)
                    Yaml.default.encodeToString(ymlObject)
                }
                else -> return echo("❌ Error: Unsupported file type '${targetFile.extension}'. Use .json, .yml, or .yaml.", err = true)
            }
            echo(formatted)
        }catch (e:Exception){
            echo("❌ Error formatting file: ${e.message}", err = true)
        }
    }
}

class Git : CliktCommand() {
    override fun run() = Unit
}

class GitLog : CliktCommand(name = "log") {
    private val count by option("-n", help = "Number of commits to show").int().default(10)
    override fun run() {
        runGitCommand("git log -n $count --oneline")
    }
}

class GitBranches : CliktCommand(name = "branches") {
    override fun run() {
        runGitCommand("git branch")
    }
}

// Helper function to run external commands
private fun CliktCommand.runGitCommand(command: String) {
    try {
        val process = ProcessBuilder(command.split(" "))
            .directory(currentDirectory)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        process.waitFor(10, TimeUnit.SECONDS)
        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()

        if (process.exitValue() != 0) {
            echo("❌ Git Error: ${error.trim()}", err = true)
        } else {
            echo(output)
        }
    } catch (e: IOException) {
        echo("❌ Error: 'git' command not found. Is Git installed and in your PATH?", err = true)
    }
}