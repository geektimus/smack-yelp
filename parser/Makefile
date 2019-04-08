build:
	docker build -f cluster/Dockerfile -t codingmaniacs/spark_base:2.4.0 cluster
	# docker tag $(docker images | grep codingmaniacs/spark_base| awk '{print $3}') codingmaniacs/spark_base:2.4.0
	sbt package
up:
	docker network create spark-net
	docker network create backend-net
	docker-compose -f docker-compose.yml up -d
	./cassandra-provision.sh
down:
	docker-compose -f docker-compose.yml down
	docker network rm backend-net
	docker network rm spark-net
run:
	docker exec spark-master /opt/spark/bin/spark-submit --class com.codingmaniacs.spark.EntryPoint --master spark://spark-master:7077 --deploy-mode cluster --verbose /opt/yelp-parser.jar /yelp_dataset/business.json,/yelp_dataset/review.json