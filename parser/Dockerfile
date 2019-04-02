FROM codingmaniacs/spark_base:2.4.0

LABEL authors="geektimus@codingmaniacs.com"

# RUN mkdir -p /app
RUN mkdir -p /yelp_dataset
COPY target/scala-2.11/yelp-parser_2.11-0.1.jar /opt/yelp-parser.jar
COPY yelp_dataset/business.json /yelp_dataset/business.json

# ENV SPARK_MASTER_NAME spark-master
# ENV SPARK_MASTER_PORT 7077
# ENV SPARK_APPLICATION_JAR_LOCATION /app/yelp-parser.jar
# ENV SPARK_APPLICATION_MAIN_CLASS com.codingmaniacs.data.parsers.UserParser
# ENV SPARK_APPLICATION_ARGS ""

# COPY submit.sh /

# CMD ["/bin/bash", "/submit.sh"]