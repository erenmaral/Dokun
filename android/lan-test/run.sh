#!/bin/bash
# LanServerPlugin JVM deneme duzenegi: bagimliliklari indirir, derler, calistirir.  Kullanim: ./run.sh [senaryo]
set -e
cd "$(dirname "$0")"
LIB=${LAN_TEST_LIB:-/tmp/dokun-lan-test/lib}; OUT=${LAN_TEST_OUT:-/tmp/dokun-lan-test/cls}
mkdir -p "$LIB" "$OUT"
get() { [ -s "$LIB/$(basename "$1")" ] && [ "$(wc -c < "$LIB/$(basename "$1")")" -gt 1000 ] || curl -fsS -o "$LIB/$(basename "$1")" "https://repo1.maven.org/maven2/$1"; }
get org/java-websocket/Java-WebSocket/1.5.7/Java-WebSocket-1.5.7.jar
get org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar
get org/json/json/20240303/json-20240303.jar
CP="$LIB/Java-WebSocket-1.5.7.jar:$LIB/slf4j-api-2.0.13.jar:$LIB/json-20240303.jar"
rm -rf "$OUT"/*
javac -nowarn -d "$OUT" -cp "$CP" $(find stubs -name '*.java') ../app/src/main/java/com/erenmaral/dokun/LanServerPlugin.java Harness.java Serve.java
exec java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp "$OUT:$CP" Harness "${1:-all}"
