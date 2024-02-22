#!/bin/bash

echo -e "Checking .env file......"
if [ ! -f .env ]; then
    echo -e "Please create a .env file from .env.example by running the following command"
    echo -e "$ cp .env.example .env"
    exit 1
fi

echo -e "Checking GOOGLE_MAPS_API_KEY......"
. .env
if [ -z "$GOOGLE_MAPS_API_KEY" ]; then
    echo -e "Please set GOOGLE_MAPS_API_KEY in .env file"
    exit 1
fi
unset GOOGLE_MAPS_API_KEY

echo -e "Restarting the application......"
echo -e "$ docker compose down --remove-orphans"
docker compose down --remove-orphans

echo -e "Building image......"
echo -e "$ docker compose build"
docker compose build

echo -e "Starting containers in background......"
echo -e "$ docker compose up -d"
docker compose up -d

echo -e "Containers started successfully......"

