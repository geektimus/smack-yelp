build:
	sbt package
up:
	docker network create spark-net
	docker network create backend-net
	docker-compose -f docker-compose.yml up -d
down:
	docker-compose -f docker-compose.yml down
	docker network rm backend-net
	docker network rm spark-net
run:
	docker exec spark-master /opt/spark/bin/spark-submit --class com.codingmaniacs.data.parsers.UserParser --master spark://spark-master:7077 --deploy-mode cluster --verbose /opt/yelp-parser.jar /yelp_dataset/business.json