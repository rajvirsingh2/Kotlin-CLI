@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Kotlin-CLI startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and KOTLIN_CLI_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\Kotlin-CLI-1.0-SNAPSHOT.jar;%APP_HOME%\lib\kotlinx-serialization-json-jvm-1.7.0.jar;%APP_HOME%\lib\kaml-jvm-0.55.0.jar;%APP_HOME%\lib\kotlinx-serialization-core-jvm-1.7.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-2.0.0.jar;%APP_HOME%\lib\clikt-mordant-markdown-jvm.jar;%APP_HOME%\lib\clikt-mordant-jvm.jar;%APP_HOME%\lib\mordant-omnibus-jvm.jar;%APP_HOME%\lib\clikt-jvm.jar;%APP_HOME%\lib\mordant-markdown-jvm.jar;%APP_HOME%\lib\mordant-jvm-jna-jvm.jar;%APP_HOME%\lib\mordant-jvm-ffm-jvm.jar;%APP_HOME%\lib\mordant-jvm-graal-ffi-jvm.jar;%APP_HOME%\lib\mordant-jvm.jar;%APP_HOME%\lib\markdown-jvm-0.7.3.jar;%APP_HOME%\lib\colormath-jvm.jar;%APP_HOME%\lib\kotlin-stdlib-2.0.10.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-2.0.0.jar;%APP_HOME%\lib\commons-codec-1.15.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\snakeyaml-engine-2.6.jar;%APP_HOME%\lib\jna-5.14.0.jar


@rem Execute Kotlin-CLI
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %KOTLIN_CLI_OPTS%  -classpath "%CLASSPATH%" com.rajvir.kotlin_cli.MainKt %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable KOTLIN_CLI_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%KOTLIN_CLI_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
