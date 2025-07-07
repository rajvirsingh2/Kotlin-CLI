package com.rajvir.kotlin_cli.commands.sec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.codec.binary.Base64

// Parent command for security-related tools
class SecurityCommands : CliktCommand() {
    override fun run() = Unit
}

// Command to generate a random password
class PassGen : CliktCommand(name = "pass-gen") {
    // Length of the password (default 16)
    private val length by option("-l", "--length", help = "Length of the password").int().default(16)

    // Flags to exclude symbols and numbers if needed
    private val noSymbols by option("--no-symbols", help = "Exclude the symbols if you want").flag()
    private val noNumbers by option("--no-numbers", help = "Exclude the numbers if you want.").flag()

    override fun run() {
        // Define possible characters
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val symbols = "!@#$%^&*()[]{};:'/?><.,"

        // Build character pool based on options
        val charPool = StringBuilder(lower + upper)
        if (!noSymbols) charPool.append(symbols)
        if (!noNumbers) charPool.append(numbers)

        // Generate password
        val password = (1..length)
            .map { charPool.random() }
            .joinToString("")

        echo(password)
    }
}

// Command to decode a JWT token and display its header and payload
class JwtDecode : CliktCommand(name = "jwt-decode") {
    private val token by argument(help = "The JWT token string")

    // JSON parser with pretty-print enabled
    private val json = Json { prettyPrint = true }

    override fun run() {
        try {
            val parts = token.split(".")
            // A valid JWT must have 3 parts: header, payload, and signature
            if (parts.size != 3) {
                return echo("❌ Error: Invalid JWT format. It must have three parts separated by dots.", err = true)
            }

            // Decode base64 parts
            val header = String(Base64.decodeBase64(parts[0]))
            val payload = String(Base64.decodeBase64(parts[1]))

            // Display decoded header and payload in formatted JSON
            echo("--- Header ---")
            echo(json.encodeToString(Json.parseToJsonElement(header)))
            echo("\n--- Payload ---")
            echo(json.encodeToString(Json.parseToJsonElement(payload)))

        } catch (e: Exception) {
            echo("❌ Error decoding JWT: ${e.message}", err = true)
        }
    }
}