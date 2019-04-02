# SMACK Yelp DataSet

[![version](https://img.shields.io/badge/version-1.0.0-green.svg)][semver]
[![Build Status](https://travis-ci.org/geektimus/smack-yelp.svg?branch=master)][travis_url]

This project was created to process the data contained on the Yelp dataset in a effort to get some interesting data from it while using some of the Technologies of the SMACK stack. I will try to use all of them but It's not required for the end solution.

The initial idea is to download, read, transform and serve the dataset so the end user can see the data in a more simplistic way.

## Getting Started

These instructions will get you a copy of the whole pipeline ready to be used on your local machine. See deployment for notes on how to use this simple pipeline.

### Prerequisites

Since all the services on this pipeline are containerized, you won't need to install a lot of things, just Docker.

### Installing

- Clone the repository

```
git clone git@github.com:geektimus/smack-yelp.git
cd ~/smack-yelp
```

## Running the tests

This pipeline only contains test for the spark job and the akka consummers.

To run the tests just run 

```
sbt test
```

On each project [parser, serving] folders

## Usage

**TODO**

## Built With

- [Scala][scala-link] - Base language
- [Spark][spark-link] - Data processing framework
- [Akka][akka-link] - Toolkit to build message-driven apps 
- [Kafka][kafka-link] - Distributed streaming platform
- [Docker][docker-link] - Containerize the parts of the pipeline

## Useful Snippets

### Get all the available categories of all the business

```scala
    val getCategoriesAsArray = udf((cat: String) =>
      cat.split(",").filter(c => c != null).map(s => s.trim)
    )

    dataFrame
      .withColumn("categories_split", getCategoriesAsArray(col("categories")))
      .createOrReplaceTempView("business")

    dataFrame
      .drop("categories")
      .sqlContext.sql("select distinct(explode(categories_split)) categories from business where categories is not null order by categories asc")
      .collect.foreach(println)
```

## Contributing

Please read [CONTRIBUTING.md][contributing] for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer][semver] for versioning. For the versions available, see the [tags on this repository][project_tags].

## Authors

- **Alex Cano** - _Initial work_ - [Geektimus][profile]

See also the list of [contributors][project_contributors] who participated in this project.

## License

[![license](https://img.shields.io/badge/license-MIT-blue.svg)][license]

This project is licensed under the MIT License - see the [LICENSE.md][license] file for details

[travis_url]: https://travis-ci.org/geektimus/smack-yelp
[scala-link]: https://www.scala-lang.org
[spark-link]: https://spark.apache.org
[akka-link]: https://akka.io
[kafka-link]: https://kafka.apache.org
[docker-link]: https://www.docker.com
[contributing]: CONTRIBUTING.md
[semver]: http://semver.org/
[project_tags]: https://github.com/geektimus/smack-yelp/tags
[profile]: https://github.com/Geektimus
[project_contributors]: https://github.com/geektimus/smack-yelp/graphs/contributors
[license]: LICENSE.md
