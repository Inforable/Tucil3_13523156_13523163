#!/bin/bash

mkdir -p bin/algoritma
mkdir -p bin/main
mkdir -p bin/model
mkdir -p bin/utils

javac -d bin src/algoritma/*.java src/main/*.java src/model/*.java src/utils/*.java

java -cp bin main.Main
