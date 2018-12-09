#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "tomforbes" --password-stdin
docker push $0
