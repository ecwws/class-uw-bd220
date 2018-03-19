import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql._

object MeetupAnalysis {

  def main(args: Array[String]) {

    val spark = SparkSession
      .builder
      .appName("Meetup Analysis")
      .getOrCreate()

    val sc = spark.sparkContext

    val schema = (new StructType()
      add("group",
        new StructType()
          add("group_city", StringType)
          add("group_state", StringType)
          add("group_country", StringType)
      )
    )

    val df = (
      spark.readStream.format("kafka")
      option("kafka.bootstrap.servers", "localhost:29092")
      option("subscribe", "meetup")
      load()
    )

    val data =
      df.select(col("key").cast("string"),
                from_json(col("value").cast("string"), schema).alias("parsed"))

    val result = (
      data.select(
        "parsed.group.group_country",
        "parsed.group.group_state",
        "parsed.group.group_city"
      )
    )

    val query = data.writeStream.format("console").start

    query.awaitTermination
  }

}

