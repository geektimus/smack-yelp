#!/bin/bash

isCassandraReady=$(docker exec -ti cassandra nodetool status | grep UN | wc -l)
while [[ ${isCassandraReady} -eq 0 ]]
do
   echo "Waiting for cassandra to be UP and Normal (UN)"
   isCassandraReady=$(docker exec -ti cassandra nodetool status | grep UN | wc -l)
done

echo "Cassandra is ready, loading keyspace and tables"
sleep 10
docker exec -ti cassandra cqlsh -f /cassandra-data/ddl.sql