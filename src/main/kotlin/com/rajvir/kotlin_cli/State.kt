package com.rajvir.kotlin_cli

import java.io.File

//Used for maintaining data change during runtime, holds shared and mutable state of our application

//A key-value pairs for in-memory store
val keyValueStore = mutableMapOf<String, String>()

//Current directory for file operations
var currentDirectory = File(System.getProperty("user.dir"))