#!/bin/bash

kafka-console-consumer.sh --topic processed-applications --from-beginning --bootstrap-server localhost:9092