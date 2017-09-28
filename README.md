# Car Ads Insight

## Solution details
It's a Java application that uses Spark to transform the input api log files into a CSV file then the CSV is loaded into
a H2 database.

It also provides the following features:
- Uses Maven to manage the software lifecycle.
- Uses JUnit to run the localhost tests.
- Uses spark-testing-base to run Spark in localhost mode.
- Uses an in-memory H2 database to analyse the data

### Implementation details
The idea is to be able to load the CSV file into **any external system** (H2, RDBMS, Kafka, Redshift, etc.). This
solution implements an H2 database as target system but an engine could be built to support more systems and the CSV
file is a quite generic format which is supported for most of these systems, so we can choose the system which fits
better to analyze the data or provide the better tools to achieve it.
