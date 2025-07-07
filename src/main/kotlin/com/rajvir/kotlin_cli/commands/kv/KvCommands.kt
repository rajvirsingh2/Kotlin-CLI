package com.rajvir.kotlin_cli.commands.kv

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.rajvir.kotlin_cli.AppContext
import com.rajvir.kotlin_cli.storage.Store

// Commands for all key-value store functionalities

// Command to store a key-value pair
class Set : CliktCommand() {
    // Access the shared application context
    private val context by requireObject<AppContext>()
    // Key to store
    private val key by argument(help = "The key to store the value.")
    // Value to associate with the key
    private val value by argument(help = "The value to save.")

    override fun run() {
        // If verbose mode is enabled, print debug info
        if (context.verbose) {
            echo("Verbose: Saving key='$key', value='$value' to $Store")
        }
        // Store the key-value pair
        Store[key] = value
        echo("Stored $key to $value")
    }
}

// Command to retrieve a value by its key
class Get : CliktCommand() {
    // Key to look up
    private val key by argument(help = "The key of the value to retrieve")

    override fun run() {
        // Show the value or an error if key not found
        Store[key]?.let(::echo) ?: echo("❌ Error: Key '$key' not found.", err = true)
    }
}

// Command to list all stored key-value pairs
class List : CliktCommand() {
    override fun run() {
        // Check if the store is empty
        if (Store.isEmpty()) {
            echo("Store is empty.")
        } else {
            echo("Stored key-value pairs:")
            // Print each key-value pair
            Store.entries().forEach { (k, v) ->
                echo("-$k: \"$v\"")
            }
        }
    }
}