package com.codingmaniacs.data.utils

class UsefulQueries {
}

object UsefulQueries {
  def main(args: Array[String]): Unit = {

    import org.apache.spark.sql.SparkSession

    val sparkSession = SparkSession
      .builder()
      .appName("Exploratory Analysis of Yelp Dataset")
      .config("sparkSession.some.config.option", "some-value")
      .master("local[*]")
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    //    import sparkSession.implicits._

    //    val usersPath = "yelp_dataset/user.json"
    //    val userDF = sparkSession.read.json(usersPath)
    //    userDF.printSchema()
    //    userDF.show(10)

    //    val reviewsPath = "yelp_dataset/review.json"
    //    val reviewDF = sparkSession.read.json(reviewsPath)
    //    reviewDF.printSchema()
    //    reviewDF.show(10)

    //    val photosPath = "yelp_dataset/photo.json"
    //    val photoDF = sparkSession.read.json(photosPath)
    //    photoDF.printSchema()
    //    photoDF.show(10)

    //    val business = "yelp_dataset/business.json"
    //    val businessDF = sparkSession.read.json(business)
    //    businessDF.printSchema()
    //    businessDF.show(10)

    //    val checkin = "yelp_dataset/checkin.json"
    //    val checkinDF = sparkSession.read.json(checkin)
    //    checkinDF.printSchema()
    //    checkinDF.show(10)
    //
    //    val tip = "yelp_dataset/tip.json"
    //    val tipDF = sparkSession.read.json(tip)
    //    tipDF.printSchema()
    //    tipDF.select("text").show(10, truncate = false)

    val businessPath = "yelp_dataset/business.json"
    val businessDF = sparkSession.read.json(businessPath)

    val reviewsPath = "yelp_dataset/business.json"
    val reviewsDF = sparkSession.read.json(reviewsPath)

    businessDF.createOrReplaceTempView("business")
    reviewsDF.createOrReplaceTempView("reviews")

    sparkSession.udf.register("categories_arr", (cat: String) =>
      if (cat!=null) cat.split(",").filter(c => c != null).map(s => s.trim) else Array[String]())

    val sqlCtx = sparkSession.sqlContext

    sqlCtx.sql(
      """
        |SELECT categories_arr(categories) categories
        |FROM business
      """.stripMargin
    ).createOrReplaceTempView("categories")

    sqlCtx.sql(
      """
        |SELECT distinct(explode(categories)) categories
        |FROM categories
        |WHERE categories is not null
        |ORDER BY categories asc
      """.stripMargin)
      .collect.foreach(println)

    sqlCtx.sql(
      """
        |SELECT city, sum(review_count) total_reviews
        |FROM business
        |GROUP BY city
        |ORDER BY total_reviews desc
      """.stripMargin
    ).limit(10).collect().foreach(println)
  }
}