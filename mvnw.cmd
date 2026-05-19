@echo off
@REM Maven Wrapper - Uses locally installed Maven from .tools directory
set "MVN_HOME=%~dp0.tools\apache-maven-3.9.16"
"%MVN_HOME%\bin\mvn.cmd" %*
