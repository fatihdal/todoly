#!/bin/bash -ex

eval $(aws ecr get-login --region us-east-1 --no-include-email)
docker run --rm -d -p 8080:8080 --name todoly-app 962253134326.dkr.ecr.us-east-1.amazonaws.com/todoly:0.0.1
