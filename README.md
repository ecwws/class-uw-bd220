# class-uw-bd220

### Prerequsite

* ruby
* ruby-dev
* ruby-bundler
* sbt
* docker
* docker-compose

### build fluent container with kafka and

```bash
cd docker/fluent/
sudo docker build -t fluent .
cd ../..
```

### start docker compose

```bash
cd docker
sudo docker-compose up -d
cd ..
```

### build job

```bash
cd meetup-analysis
sbt package
cd ..
```

### start job

These are the parameters used for testing the job:

```bash
spark-submit \
  --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.3.0 \
  --driver-java-options="-XX:+UseG1GC -XX:MaxGCPauseMillis=20 \
  -XX:InitiatingHeapOccupancyPercent=35 -XX:+ExplicitGCInvokesConcurrent" \
  --driver-memory=4g --executor-memory=20g \
  meetup-analysis/target/scala-2.11/meetup-analysis_2.11-1.0.jar
```

### build and run producer

```bash
cd meetup-rsvp-producer
bundle
ruby meetup-rsvp-producer.rb
```

### service endpoints

* kafka - localhost:29092
* elasticsearch - localhost:9200
