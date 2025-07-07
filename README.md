# Kotlin CLI: The All-in-One Command-Line Toolkit

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/rajvir31/Kotlin-CLI)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Welcome to **Kotlin CLI**, a powerful, multipurpose command-line interface built with Kotlin and the Clikt library. This tool is designed to be a developer's Swiss Army knife, providing a wide range of utilities from file system manipulation and networking to AI-powered queries and productivity workflows, all accessible from a single, consistent interface.

This project was built to demonstrate a deep understanding of modern software engineering principles, including clean architecture, dependency management, multi-platform deployment via Docker, and integration with external APIs.

---

## Features

This CLI is organized into several command groups, each with its own set of powerful features:

* **`kv` (Key-Value Store):** A persistent, file-based key-value store for saving and retrieving configuration or small pieces of data.
* **`fs` (File System):** A comprehensive suite of file utilities, including a directory tree viewer, file search with filters, checksum generation, and a safe temporary file cleaner.
* **`dev` (Developer Tools):** Handy tools for developers, such as a local HTTP server, a multi-language code snippet runner (supporting Java, Kotlin, C++, Go, and more), and a Git wrapper for common commands.
* **`net` (Networking):** A collection of networking utilities, including a `ping` tool, a full HTTP client similar to `curl`, a port scanner, and a file downloader.
* **`ai` (Artificial Intelligence):** Interact with Google's Gemini AI directly from your terminal to ask questions and get intelligent answers.
* **`security` (Security):** Tools for security-related tasks, including a secure password generator and a JWT token decoder.
* **`prod` (Productivity):** A persistent, file-based To-Do list manager with `add`, `list`, and `done` commands.
* **`fun` (Fun):** A classic `cowsay` clone to bring some levity to your terminal.

---

## Installation

The easiest and recommended way to install and use the Kotlin CLI is with Docker. This ensures that you don't need to install Java, Kotlin, or any other dependencies on your system.

### Prerequisites

1.  **Docker:** You must have Docker installed on your system. You can get it from the [official Docker website](https://www.docker.com/products/docker-desktop/).
2.  **(Optional) Gemini API Key:** To use the `ai` commands, you need an API key from Google AI Studio.

### Installation Steps

These steps will set up a global `kv` command on your system (for macOS and Linux).

**1. Create the Helper Script**

This script will automatically handle the complex `docker run` commands for you.

* Open your terminal and run the following command to create and open a new file named `kv` in `/usr/local/bin` using the `nano` text editor:

    ```bash
    sudo nano /usr/local/bin/kv
    ```

  *(You will be prompted for your password as this requires administrator privileges.)*

**2. Paste the Script Content**

* Copy the entire code block below. **Make sure to replace `rajvir31` with your own Docker Hub username if you are deploying your own version.**

    ```bash
    #!/bin/bash
    # Helper script to run the Kotlin CLI via Docker
    
    # The public Docker Hub image for the CLI
    DOCKER_IMAGE="rajvir31/kotlin-cli:1.1.0"
    
    # This command runs the Docker container with the correct settings:
    # --rm      : Deletes the container after it runs to keep your system clean.
    # -it       : Allows for interactive prompts.
    # -v ...    : Creates persistent storage for the app's data.
    # -v ...    : Maps your current directory into the container.
    # -w ...    : Sets the working directory inside the container.
    # -e ...    : Passes your Gemini API key into the container.
    # "$@"      : Passes all of your commands (e.g., "fs tree") to the CLI.
    docker run --rm -it \
      -v kotlin-cli-data:/root/.kotlin-cli \
      -v "$(pwd)":/app/work \
      -w /app/work \
      -e GEMINI_API_KEY="$GEMINI_API_KEY" \
      "$DOCKER_IMAGE" "$@"
    ```

* Paste the copied script into the `nano` editor.

**3. Save and Exit**

* Press `Ctrl + X` to exit.
* Press `Y` to confirm you want to save the changes.
* Press `Enter` to confirm the file name.

**4. Make the Script Executable**

* You need to give your system permission to run this file as a command.

    ```bash
    sudo chmod +x /usr/local/bin/kv
    ```

**Installation is complete!** You can now use the `kv` command from anywhere in your terminal.

---

## Usage

Open a new terminal window and try it out!

### First Run & Getting Help

```bash
# See the version number
kv --version

# See the list of all top-level commands
kv --help
```

### AI Commands (Setup Required)

To use the `ai ask` command, you must first set your Gemini API key as an environment variable.

```bash
# Add this line to your ~/.bashrc, ~/.zshrc, or other shell profile file
export GEMINI_API_KEY="YOUR_API_KEY_HERE"

# Then, you can ask questions
kv ai ask "What is the difference between a compiler and an interpreter?"
```

### Command Examples

Here are a few examples to get you started:

```bash
# Get a list of all files in the current directory
kv fs tree --depth 1

# Make an HTTP GET request
kv net http [https://api.github.com](https://api.github.com)

# Generate a secure 24-character password
kv security pass-gen -l 24

# Add an item to your to-do list
kv prod todo add "Read the Kotlin CLI README"

# See what the cow has to say
kv fun cowsay "This CLI is amazing!"
```

---

## Building From Source

If you want to modify the code and build the project yourself, you can clone the repository and use Gradle.

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/rajvir31/Kotlin-CLI.git](https://github.com/rajvir31/Kotlin-CLI.git)
    cd Kotlin-CLI
    ```

2.  **Build the project:**
    ```bash
    ./gradlew build
    ```

3.  **Run commands locally:**
    ```bash
    ./gradlew run -- <your commands here>
    # Example:
    ./gradlew run -- fs tree
    ```

---

## Technology Stack

* **Language:** [Kotlin](https://kotlinlang.org/)
* **CLI Framework:** [Clikt](https://github.com/ajalt/clikt)
* **Build Tool:** [Gradle](https://gradle.org/)
* **Deployment:** [Docker](https://www.docker.com/)
