@echo off
set JAVA_HOME=E:\Environment\jdk21
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d C:\桌面\code\badminton-backend
E:\Environment\apache-maven-3.9.4\bin\mvn.cmd spring-boot:run