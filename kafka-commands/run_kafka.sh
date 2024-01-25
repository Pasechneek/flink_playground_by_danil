#!/bin/bash

kafka-server-stop.sh
zkServer.sh stop
zkServer.sh start
kafka-server-start.sh /opt/Kafka/kafka_2.12-3.6.0/config/server.properties