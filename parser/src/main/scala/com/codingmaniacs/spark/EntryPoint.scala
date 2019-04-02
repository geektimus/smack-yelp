package com.codingmaniacs.spark

import org.apache.spark.sql.SparkSession

object EntryPoint {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession
      .builder()
      .appName("Exploratory Analysis of Yelp Dataset")
      .master("local[*]")
      .getOrCreate()

    val reviewsFilePath = "./yelp_dataset/review.json"
    val businessFilePath = "./yelp_dataset/business.json"
    val usersFilePath = "./yelp_dataset/user.json"

    val reviewDataFrame = sparkSession.read.json(reviewsFilePath)
    reviewDataFrame.createOrReplaceTempView("reviews")


    // Reviews
    val reviewsWithAVGStarsDF = reviewDataFrame.sqlContext.sql(
      """
        |SELECT business_id, bround(avg(stars), 1) stars
        |FROM reviews
        |GROUP BY business_id
      """.stripMargin
    ).cache()
    // Show pet friendly business

    val businessDataFrame = sparkSession.read.json(businessFilePath)
    businessDataFrame.createOrReplaceTempView("business")

    val petFriendlyRestaurants = businessDataFrame.sqlContext.sql(
      """
        |SELECT name, attributes.DogsAllowed, review_count, business_id
        |FROM business
        |WHERE attributes.DogsAllowed = True and categories like '%Restaurant%'
      """.stripMargin
    )

    val petFriendlyRestaurantsReviews = petFriendlyRestaurants.join(
      reviewsWithAVGStarsDF,
      Seq("business_id"),
      "inner"
    )

    petFriendlyRestaurantsReviews.createOrReplaceTempView("business_reviews")

    // Top 10 pet friendly restaurants with the highest start count
    petFriendlyRestaurantsReviews
      .sqlContext.sql(
      """
        |SELECT name business_name, review_count reviews, stars rating
        |FROM business_reviews
        |ORDER BY stars desc, review_count desc
      """.stripMargin)
      .show(10, truncate = false)

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

    val reviewsByPriceRange = restaurants.join(
      reviews,
      Seq("business_id"),
      "inner"
    )

    reviewsByPriceRange.createOrReplaceTempView("reviews_by_business_price")

    reviewsByPriceRange
      .sqlContext
      .sql(
        """
          |SELECT bround(avg(stars), 1) rating, price_range
          |FROM reviews_by_business_price
          |GROUP BY price_range
          |ORDER BY price_range desc
        """.stripMargin
      ).show(10, truncate = false)

    // The user that has contributed the most reviews

//    val usersDataFrame = sparkSession.read.json(usersFilePath)
//    usersDataFrame.createOrReplaceTempView("users")
//
//    usersDataFrame.sqlContext.sql(
//      """
//        |SELECT name
//      """.stripMargin)
  }
}
