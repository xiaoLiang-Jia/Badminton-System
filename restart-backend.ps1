$env:JAVA_HOME = "E:\Environment\jdk21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
Set-Location "C:\桌面\code\badminton-backend"
& "E:\Environment\apache-maven-3.9.4\bin\mvn.cmd" spring-boot:run