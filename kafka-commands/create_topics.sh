#!/bin/bash

kafka-topics.sh --create --topic caught-applications --bootstrap-server localhost:9092
kafka-topics.sh --create --topic processed-applications --bootstrap-server localhost:9092