package com.rajvir.kotlin_cli.commands.net

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.rajvir.kotlin_cli.currentDirectory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.TimeUnit

//Parent Command
class NetCommands: CliktCommand(name = "net"){
    override fun run()=Unit
}

class Ping: CliktCommand(){
    private val host by argument(help = "The hostname or IP address to ping")
    private val count by option("-c", "--count", help = "Number of pings to send").int().default(4)

    override fun run() {
        val command = if(System.getProperty("os.name").lowercase().contains("win")){
            listOf("ping", "-n", count.toString(), host)
        }else{
            listOf("ping", "-c", count.toString(), host)
        }
        try{
            val process = ProcessBuilder(command).inheritIO().start()
            process.waitFor(30, TimeUnit.SECONDS)
            if(process.exitValue() != 0){
                echo("❌ Ping failed for host: $host", err = true)
            }
        }catch (e: IOException){
            echo("❌ Error: 'ping' command not found. Is it installed and in your PATH?", err = true)
        }
    }
}

class Request: CliktCommand(name = "http"){
    private val url by argument(help = "The URL to request")
    private val headers by option("-H", "--header", help = "Add a request header (e.g., 'Content-Type: application/json')").multiple()

    override fun run() {
        val client = HttpClient.newBuilder().build()
        val requestBuilder = HttpRequest.newBuilder().uri(URI.create(url))

        headers.forEach {
            val parts = it.split(":", limit = 2)
            if(parts.size == 2){
                requestBuilder.headers(parts[0].trim(), parts[1].trim())
            }
        }

        try{
            val response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
            echo("Status: ${response.statusCode()}")
            echo("Headers: ")
            response.headers().map().forEach { (k,v) -> echo("  $k: ${v.joinToString(", ")}") }
            echo("\nBody: ")
            echo(response.body())
        }catch (e:Exception){
            echo("❌ Error making request: ${e.message}", err = true)
        }
    }
}

class Scan : CliktCommand(name = "port-scan") {
    private val host by argument(help = "The hostname or IP to scan")
    private val ports by option("-p", "--ports", help = "Port range to scan (e.g., 1-1024)").default("1-1024")
    override fun run() {
        val (start, end) = ports.split("-").map { it.toInt() }
        echo("Scanning $host from port $start to $end...")
        for (port in start..end) {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, port), 200) // 200ms timeout
                    echo("✅ Port $port is open")
                }
            } catch (e: IOException) {
                // Port is closed or filtered
            }
        }
        echo("Scan complete.")
    }
}

class Download : CliktCommand() {
    private val url by argument(name = "URL", help = "The URL of the file to download")
    private val outputFile by argument(name = "OUTPUT_FILE").optional()
    override fun run() {
        val uri = URI.create(url)
        val filename = outputFile ?: uri.path.substring(uri.path.lastIndexOf('/') + 1)
        val targetFile = File(currentDirectory, filename)

        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder(uri).build()

        echo("Downloading $url to ${targetFile.path}...")
        client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body().use { input ->
            FileOutputStream(targetFile).use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            }
        }
        echo("✅ Download complete: ${targetFile.name}")
    }
}
