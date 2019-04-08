package com.codingmaniacs.spark

import com.codingmaniacs.data.utils.SentimentAnalysisUtils
import org.apache.spark.sql.{SaveMode, SparkSession}

object EntryPoint {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession
      .builder()
      .appName("Exploratory Analysis of Yelp Dataset")
      .master("spark-master:7077")
      .config("spark.cassandra.connection.host", "cassandra")
      .config("spark.cassandra.connection.port", "9042")
      .getOrCreate()

    val reviewsFilePath = "/yelp_dataset/review.json"
    val businessFilePath = "/yelp_dataset/business.json"

    val reviewDataFrame = sparkSession.read.json(reviewsFilePath)
    reviewDataFrame.createOrReplaceTempView("reviews")
    val businessDataFrame = sparkSession.read.json(businessFilePath)
    businessDataFrame.createOrReplaceTempView("business")

    val sqlCtx = sparkSession.sqlContext

    // Reviews
    val reviewsWithAVGStarsDF = reviewDataFrame.sqlContext.sql(
      """
        |SELECT business_id, bround(avg(stars), 1) stars
        |FROM reviews
        |GROUP BY business_id
      """.stripMargin
    )

    // Show pet friendly business
    val petFriendlyRestaurants = businessDataFrame.sqlContext.sql(
      """
        |SELECT name, attributes.DogsAllowed, review_count, business_id
        |FROM business
        |WHERE attributes.DogsAllowed = True and categories like '%Restaurant%'
      """.stripMargin
    )

    petFriendlyRestaurants.join(
      reviewsWithAVGStarsDF,
      Seq("business_id"),
      "inner"
    ).createOrReplaceTempView("business_reviews")

    // Top 10 pet friendly restaurants with the highest start count
    sqlCtx.sql(
      """
        |SELECT business_id id, name business_name, review_count reviews, stars rating
        |FROM business_reviews
        |ORDER BY stars desc, review_count desc
      """.stripMargin)
      .write
      .mode(SaveMode.Append)
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "pet_friendly_restaurants", "keyspace" -> "analysis"))
      .save()


    // is the price of the restaurants related to their stars?
    val restaurants = businessDataFrame.sqlContext.sql(
      """
        |SELECT business_id, attributes.RestaurantsPriceRange2 price_range
        |FROM business
        |WHERE categories like '%Restaurant%' and
        |attributes.RestaurantsPriceRange2 is not null AND
        |attributes.RestaurantsPriceRange2 <> 'None'
      """.stripMargin
    )

    val reviews = reviewDataFrame.sqlContext.sql(
      """
        |SELECT stars, business_id
        |FROM reviews
        |WHERE stars is not null
      """.stripMargin
    )

    restaurants.join(
      reviews,
      Seq("business_id"),
      "inner"
    ).createOrReplaceTempView("reviews_by_business_price")


    sqlCtx.sql(
      """
        |SELECT bround(avg(stars), 1) rating, price_range
        |FROM reviews_by_business_price
        |GROUP BY price_range
        |ORDER BY price_range desc
      """.stripMargin
    )
      .write
      .mode(SaveMode.Append)
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "rating_with_price_range", "keyspace" -> "analysis"))
      .save()

    // The user that has contributed the most reviews

    sparkSession.udf.register("sentiment", (text: String) => SentimentAnalysisUtils.detectSentiment(text).toString)

    // Reduce the search to only 100 restaurants in Montréal
    sqlCtx.sql(
      """
        |SELECT business_id
        |FROM business
        |WHERE city = 'Montréal' and categories like '%Restaurant%'
        |LIMIT 100
      """.stripMargin).createOrReplaceTempView("montreal_restaurants")

    sqlCtx.sql(
      """
        |SELECT mr.business_id, user_id, text, sentiment(text) sentiment
        |FROM montreal_restaurants mr, reviews r
        |WHERE mr.business_id = r.business_id
      """.stripMargin
    )
      .createOrReplaceTempView("reviews_with_sentiment")

    // Number of reviews per user which are positive or very_positive
    sqlCtx.sql(
      """
        |SELECT user_id, sentiment, count(1) count
        |FROM reviews_with_sentiment
        |WHERE sentiment in ('POSITIVE', 'VERY_POSITIVE')
        |GROUP BY user_id, sentiment
      """.stripMargin
    )
      .write
      .mode(SaveMode.Append)
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "user_review_sentiment", "keyspace" -> "analysis"))
      .save()

  }

}
