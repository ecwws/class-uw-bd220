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
      add("mtime", LongType)
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
        from_unixtime((col("parsed.mtime")/1000).cast(IntegerType)).as("timestamp"),
        col("parsed.group.group_country"),
        col("parsed.group.group_state"),
        col("parsed.group.group_city")
      )
    )

    val query = data.writeStream.format("console").start

    query.awaitTermination
  }

}

