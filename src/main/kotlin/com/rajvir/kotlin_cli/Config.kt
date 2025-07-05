package com.rajvir.kotlin_cli

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths

//Holds application's configurations
@Serializable
data class Config(var defaultLog: String = "INFO")

//Manages loading and saving the configuration
object ConfigLoader{
    private val configPath = Paths.get(System.getProperty("user.home"), ".kotlin-cli", "config.json")
    private val json = Json { prettyPrint = true }

    fun load(): Config{
        return if(Files.exists(configPath)){
            val content = Files.readString(configPath)
            json.decodeFromString<Config>(content)
        }else{
            //Default config if nothing exists
            Config()
        }
    }

    fun save(config: Config){
        Files.createDirectories(configPath.parent)
        val content = json.encodeToString(config)
        Files.writeString(configPath, content)
    }
}