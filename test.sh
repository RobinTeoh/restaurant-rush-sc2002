#!/bin/sh
# Compile tests in test/ and run them with the bundled JUnit (run build.sh first).
set -e
cd "$(dirname "$0")"
JUNIT=lib/junit-platform-console-standalone-1.10.2.jar
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";" ;; *) SEP=":" ;; esac   # Java on Windows uses ';'
rm -rf out-test; mkdir -p out-test
javac --release 17 -d out-test -cp "out${SEP}${JUNIT}" $(find test -name "*.java")
java -jar "$JUNIT" execute -cp "out${SEP}out-test" --scan-class-path
