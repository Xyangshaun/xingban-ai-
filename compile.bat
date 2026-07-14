@echo off
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
echo Java Home: %JAVA_HOME%
java -version
echo.
echo Compiling Kotlin...
.\gradle-8.10.2\bin\gradle.bat clean compileDebugKotlin --no-daemon