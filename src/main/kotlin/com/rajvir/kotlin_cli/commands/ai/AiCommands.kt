package com.rajvir.kotlin_cli.commands.ai

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument

class AiCommands: CliktCommand() {
    override fun run()=Unit
}

class TextSummarize:CliktCommand(name="text-summarize"){
    private val text by argument("The text to summarize")
    override fun run() {
        //Basic Summarization
        val sentences = text.split(". ", "! ", "? ")
        val words = text.lowercase().split(Regex("\\W+")).filter { it.length>3 }
        val wordFreq = words.groupingBy { it }.eachCount()

        val sentenceScore = sentences.map { sentence ->
            sentence.lowercase().split(Regex("\\W+")).sumOf { word->
                wordFreq[word]?:0
            }
        }
        val topSentence = sentences.zip(sentenceScore)
            .sortedByDescending { it.second }
            .take(3) //Top 3 sentences
            .map { it.first }

        echo("Summary: ")
        topSentence.forEach { echo("- $it") }
    }
}