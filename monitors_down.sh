#!/bin/bash

docker-compose -f docker-composem1.yml down
docker-compose -f docker-composem2.yml down
docker rmi iotmonitorl1m1
docker rmi iotmonitorl1m2