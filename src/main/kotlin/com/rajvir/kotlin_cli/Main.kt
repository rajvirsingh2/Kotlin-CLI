package com.rajvir.kotlin_cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.rajvir.kotlin_cli.commands.fs.*
import com.rajvir.kotlin_cli.commands.kv.*
import com.rajvir.kotlin_cli.storage.Store
import java.io.File

var currentDirectory: File = Store.currentDir()

class Kv:CliktCommand(){
    override fun run() {
        echo("Current directory: ${currentDirectory.absolutePath}")
        echo("No subcommand was used. Use --help for a list of commands.")
    }
}

fun main(args: Array<String>){
    Kv().subcommands(
        //K-V commands
        Set(), Get(), List(),
        //File manipulation commands
        CreateFile(), DeleteFile(), WriteFile(), ReadFile(), Cd()
    ).main(args)
}