#!/bin/bash

curr_path=$(cd $(dirname $0); pwd)
if [[ $(docker inspect nginx-server -f '{{ .State.Status}}')  == "running" ]]; then
    echo "already running."
else
docker-compose -f ${curr_path}/docker-compose.yml up -d
docker cp /data/trigger/stateful-test-for-http-0.14-SNAPSHOT.jar nginx-server:/usr/share/nginx/html/
docker cp /data/udf/upload-http-udf-0.14-SNAPSHOT.jar nginx-server:/usr/share/nginx/html/
fi
echo "done"
#wget http://127.0.0.1:8085/stateful-test-for-http-0.14-SNAPSHOT.jar
