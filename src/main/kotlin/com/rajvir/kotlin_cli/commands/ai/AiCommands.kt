package com.rajvir.kotlin_cli.commands.ai

// Imports for CLI, serialization, networking, etc.
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

// Base command group for AI-related subcommands
class AiCommands : CliktCommand() {
    override fun run() = Unit // No action for parent command itself
}

// Data class representing a single text part to send to Gemini
@Serializable
data class GeminiPart(val text: String)

// Wrapper for parts to be included in a content block
@Serializable
data class GeminiContent(val parts: List<GeminiPart>)

// Payload that wraps all contents being sent to Gemini
@Serializable
data class GeminiPayload(val contents: List<GeminiContent>)

// Response format from Gemini API
@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiError? = null
)

// Represents a single candidate response from Gemini
@Serializable
data class GeminiCandidate(val content: GeminiContent)

// Error object if Gemini returns an error
@Serializable
data class GeminiError(
    val code: Int? = null,
    val message: String,
    val status: String? = null
)

// Helper function to load API key from secrets.properties file
private fun loadAPIKey(): String? {
    val file = File("secrets.properties")
    if (!file.exists()) return null

    val properties = Properties()
    file.inputStream().use { properties.load(it) }
    return properties.getProperty("API-KEY")
}

// Command to ask a question using the Gemini API
class Ask : CliktCommand() {
    // Argument to capture the question as a list of words
    private val question by argument(help = "The question to ask").multiple(required = true)

    override fun run() {
        // Load API key from file or environment
        val apiKey = loadAPIKey() ?: System.getenv("API-KEY")
        if (apiKey.isNullOrBlank()) {
            echo("❌ Error: API key not found in secrets.properties or environment.", err = true)
            return
        }

        // Combine input into a full question string
        val fullQuestion = question.joinToString(" ")
        echo("🤔 Thinking...")

        // Setup HTTP client and JSON parser
        val client = HttpClient.newBuilder().build()
        val json = Json { ignoreUnknownKeys = true }

        // Build payload for the API request
        val payload = GeminiPayload(
            listOf(GeminiContent(listOf(GeminiPart(fullQuestion))))
        )
        val requestBody = json.encodeToString(payload)

        // Gemini model and API endpoint
        val model = "gemini-2.5-flash"
        val endpoint =
            "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        // Create HTTP POST request
        val request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        try {
            // Send request and get response
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val responseBody = response.body()
            val geminiResponse = json.decodeFromString<GeminiResponse>(responseBody)

            if (response.statusCode() == 200) {
                // Extract and display the answer
                val answer = geminiResponse.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (answer != null) {
                    echo("\n🤖 Gemini says:")
                    echo(answer)
                } else {
                    echo("❌ Error: Could not parse AI response.", err = true)
                }
            } else {
                // Handle non-200 status codes
                echo("❌ API Error (${response.statusCode()}): ${geminiResponse.error?.message}", err = true)
            }
        } catch (e: Exception) {
            // Catch and display exceptions
            echo("❌ Error sending request to Gemini API: ${e.message}", err = true)
        }
    }
}
