#!/bin/bash

# Gradle Wrapper bootstrap script for Unix-based systems

if [ ! -f gradlew ]; then
  echo "Downloading Gradle Wrapper..."
  gradle wrapper --gradle-version 8.7
fi
./gradlew "$@"
