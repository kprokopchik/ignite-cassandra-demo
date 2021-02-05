#!/usr/bin/env bash

set -e

gradle clean build -Dorg.gradle.java.home="$(/usr/libexec/java_home -v 11)"

docker-compose up --build --force-recreate --detach
