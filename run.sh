#!/usr/bin/env bash
javac -cp "yakip.jar:lib:src" -d out src/Launcher.java

java -d64 -Xmx6500m -Xms6500m -Xss2048k -Djava.library.path=lib/native -cp "yakip.jar:lib:out" Launcher $1 $2

