package com.rajvir.kotlin_cli.commands.kv

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.rajvir.kotlin_cli.storage.Store

//Commands for all key-value store functionalities

class Set: CliktCommand(){
    private val key by argument(help = "The key to store the value.")
    private val value by argument(help = "The value to save.")
    override fun run() {
        Store[key] = value
        echo("Stored $key to $value")
    }
}

class Get: CliktCommand() {
    private val key by argument(help = "The key of the value to retrieve")
    override fun run() {
        Store[key]?.let(::echo)?:echo("❌ Error: Key '$key' not found.", err = true)
    }
}

class List: CliktCommand() {
    override fun run() {
        if(Store.isEmpty()) {
            echo("Store is empty.")
        } else{
            echo("Stored key-value pairs:")
            Store.entries().forEach{ (k,v) ->
                echo("-$k: \"$v\"")
            }
        }
    }
}