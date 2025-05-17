@echo off

if not exist bin\algoritma mkdir bin\algoritma
if not exist bin\main mkdir bin\main
if not exist bin\model mkdir bin\model
if not exist bin\utils mkdir bin\utils

javac -d bin src/algoritma/*.java src/main/*.java src/model/*.java src/utils/*.java

java -cp bin main.Main
