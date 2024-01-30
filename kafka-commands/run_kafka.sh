#!/bin/bash

kafka-server-stop.sh
zkServer.sh stop
zkServer.sh start
kafka-server-start.sh $KAFKA_HOME/config/server.properties