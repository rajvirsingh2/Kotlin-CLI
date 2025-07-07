package com.rajvir.kotlin_cli.commands.sec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int

class SecurityCommands: CliktCommand() {
    override fun run() = Unit
}

class PassGen:CliktCommand(name = "pass-gen"){
    private val length by option("-l", "--length", help = "Length of the password").int().default(16)
    private val noSymbols by option("--no-symbols", help = "Exclude the symbols if you want").flag()
    private val noNumbers by option("--no-numbers", help = "Exclude the numbers if you want.").flag()

    override fun run() {
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val symbols = "!@#$%^&*()[]{};:'/?><.,"

        val charPool = StringBuilder(lower+upper)
        if(!noSymbols) charPool.append(symbols)
        if(!noNumbers) charPool.append(numbers)

        val password = (1..length)
            .map { charPool.random() }
            .joinToString("")

        echo(password)
    }
}