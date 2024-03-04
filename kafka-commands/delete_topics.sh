#!/bin/bash

kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic caught-applications
kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic processed-applications