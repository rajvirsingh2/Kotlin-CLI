package com.rajvir.kotlin_cli.storage

import java.io.File
import java.nio.file.*
import java.util.*

object Store{
    private val file = Paths.get(System.getProperty("user.home"), ".kotlin-cli", "store.properties")
    private val props = Properties()

    init{                      // load once at startup
        if (Files.exists(file)) {
            Files.newInputStream(file).use(props::load)
        }
    }

    operator fun get(key: String): String? = props.getProperty(key)

    operator fun set(key: String, value: String){
        props[key] = value
        Files.createDirectories(file.parent)
        Files.newOutputStream(file).use { props.store(it, null) }
    }

    fun isEmpty(): Boolean = props.isEmpty

    fun entries(): Set<Map.Entry<Any, Any>> = props.entries

    fun currentDir():File =
        props.getProperty("cwd")?.let { File(it) }
            ?: File(System.getProperty("user.dir"))

    fun setCurrentDir(dir: File){
        props.setProperty("cwd", dir.absolutePath)
        file.toFile().parentFile.mkdirs()
        file.toFile().outputStream().use { props.store(it, null) }
    }

    fun save(){
        Files.createDirectories(file.parent)
        Files.newOutputStream(file).use { props.store(it, "Kotlin-CLI data") }
    }
}
