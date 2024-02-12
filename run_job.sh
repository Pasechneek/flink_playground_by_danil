#!/bin/bash

mvn -v

#mvn compile
mvn exec:java -X -Dexec.mainClass="org.example.DbTopicJob"