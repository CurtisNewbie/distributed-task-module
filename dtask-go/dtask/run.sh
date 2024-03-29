#!/bin/bash

pid=0
logfile="/usr/src/dtaskgo/logs/dtaskgo.log"
if [ ! -f "$logfile" ]; then
    touch "$logfile"
fi

trap 'kill ${!}; kill -SIGTERM "$pid"' SIGTERM

./main profile='prod' configFile=/usr/src/dtaskgo/config/app-conf-prod.json >> "$logfile" 2>&1

pid="$!"

# wait forever
while true
do
  tail -f /dev/null & wait ${!}
done
