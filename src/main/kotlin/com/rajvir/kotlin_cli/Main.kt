package com.rajvir.kotlin_cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.rajvir.kotlin_cli.commands.*


fun main(args: Array<String>) {
    RootCommand()
        .subcommands(
            FileCommands().subcommands(
                FileCreateCommand(),
                FileDeleteCommand(),
                FileCopyCommand()
            ),
            DirCommands().subcommands(
                DirCreateCommand(),
                DirDeleteCommand()
            ),
            AppCommands().subcommands(
                AppOpenCommand()
            ),
            StartShellCommand()
        )
        .main(args)
}


class RootCommand : CliktCommand() {
    override fun run() {
        // Can show default help or just remain empty
    }
}
