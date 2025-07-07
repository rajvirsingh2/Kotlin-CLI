package com.rajvir.kotlin_cli.storage

import java.io.File
import java.nio.file.*
import java.util.*

// Singleton object to handle persistent key-value storage
object Store {
    // Path to the properties file in the user's home directory
    private val file = Paths.get(System.getProperty("user.home"), ".kotlin-cli", "store.properties")

    // Properties object to hold key-value data
    private val props = Properties()

    init {
        // Load existing properties from file during initialization
        if (Files.exists(file)) {
            Files.newInputStream(file).use(props::load)
        }
    }

    // Get a value by key using bracket notation (e.g., Store["key"])
    operator fun get(key: String): String? = props.getProperty(key)

    // Set a key-value pair and save it to the file
    operator fun set(key: String, value: String) {
        props[key] = value
        Files.createDirectories(file.parent) // Ensure directory exists
        Files.newOutputStream(file).use { props.store(it, null) } // Save to file
    }

    // Check if the store is empty
    fun isEmpty(): Boolean = props.isEmpty

    // Get all key-value entries
    fun entries(): Set<Map.Entry<Any, Any>> = props.entries

    // Get the current working directory from the store or fallback to system directory
    fun currentDir(): File =
        props.getProperty("cwd")?.let { File(it) }
            ?: File(System.getProperty("user.dir"))

    // Set and save the current working directory
    fun setCurrentDir(dir: File) {
        props.setProperty("cwd", dir.absolutePath)
        file.toFile().parentFile.mkdirs() // Ensure directory exists
        file.toFile().outputStream().use { props.store(it, null) } // Save to file
    }
}
