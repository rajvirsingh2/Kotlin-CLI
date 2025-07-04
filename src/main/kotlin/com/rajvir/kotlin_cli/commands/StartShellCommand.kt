package com.rajvir.kotlin_cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import java.io.File
import java.lang.ProcessBuilder.Redirect
import java.net.URI

class StartShellCommand : CliktCommand(
    name = "start"
) {
    override fun run() {
        val os = System.getProperty("os.name").lowercase()
        try {
            when {
                // ── Windows desktop (cmd, PowerShell, Windows Terminal) ───────────────
                os.contains("win") -> {
                    // Uses the built‑in shell to spawn PowerShell in a new window
                    ProcessBuilder("cmd", "/c", "start", "powershell")
                        .redirectOutput(Redirect.INHERIT)
                        .redirectError(Redirect.INHERIT)
                        .start()
                    echo("Opening PowerShell …")
                }

                // ── WSL (inside Windows Subsystem for Linux) ─────────────────────────
                System.getenv("WSL_DISTRO_NAME") != null -> {
                    // Launch a *Windows* terminal profile so you get a separate window
                    ProcessBuilder("cmd.exe", "/c", "wt.exe", "-p", "PowerShell")
                        .inheritIO()
                        .start()
                    echo("Opening Windows Terminal → PowerShell …")
                }

                // ── macOS ────────────────────────────────────────────────────────────
                os.contains("mac") -> {
                    ProcessBuilder("open", "-a", "Terminal")
                        .inheritIO()
                        .start()
                    echo("Opening macOS Terminal …")
                }

                // ── Linux/X11/Wayland ───────────────────────────────────────────────
                else -> {                                  // Try common emulators
                    val terms = listOf(
                        "gnome-terminal", "konsole", "xfce4-terminal",
                        "x-terminal-emulator", "kitty", "alacritty", "lxterminal"
                    )
                    val term = terms.firstOrNull { which(it) }
                        ?: error("No graphical terminal emulator found")

                    ProcessBuilder(term).inheritIO().start()
                    echo("Opening $term …")
                }
            }
        } catch (t: Throwable) {
            echo("Could not open a terminal window: ${t.message}", err = true)
        }
    }

    /** Simple POSIX “which” */
    private fun which(cmd: String): Boolean =
        ProcessBuilder("which", cmd).start().waitFor() == 0
}
