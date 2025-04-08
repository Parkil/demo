#!/bin/sh

docker buildx build -t alpine/jre21 -f Dockerfile_alpine_jre21 .

docker buildx build -t demo:latest -f Dockerfile_demo .

docker compose -f docker-compose-standalone.yml up -d