package com.rajvir.kotlin_cli.commands.kv

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.rajvir.kotlin_cli.keyValueStore

//Commands for all key-value store functionalities

class Set: CliktCommand(){
    val key by argument(help = "The key to store the value.")
    val value by argument(help = "The value to save.")
    override fun run() {
        keyValueStore[key] = value
        echo("Saved $value for $key")
    }
}

class Get: CliktCommand() {
    val key by argument(help = "The key of the value to retrieve")
    override fun run() {
        val value = keyValueStore[key]
        if (value != null) {
            echo(value)
        } else {
            echo("❌ Error: Key '$key' not found.", err = true)
        }
    }
}

class List: CliktCommand() {
    override fun run() {
        if(keyValueStore.isEmpty()) {
            echo("Store is empty.")
        } else{
            echo("Stored key-value pairs:")
            keyValueStore.forEach{ (k,v) ->
                echo("-$k: \"$v\"")
            }
        }
    }
}