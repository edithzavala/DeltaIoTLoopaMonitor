#!/bin/bash

./gradlew build
docker build -f Dockerfile_m1 -t iotmonitorl1m1 .
docker build -f Dockerfile_m2 -t iotmonitorl1m2 .
docker-compose -f docker-composem1.yml up &
docker-compose -f docker-composem2.yml up &
sleep 15 #change to 10 or 5 so it fails quicker
docker-compose -f docker-composem2.yml down
docker rmi iotmonitorl1m2