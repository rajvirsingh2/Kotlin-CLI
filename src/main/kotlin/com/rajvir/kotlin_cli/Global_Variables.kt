package com.rajvir.kotlin_cli

import com.rajvir.kotlin_cli.storage.Store
import java.io.File

//Context object to hold shared state for current run
class AppContext(var verbose: Boolean = false)

//Persisted current directory
var currentDirectory: File = Store.currentDir()