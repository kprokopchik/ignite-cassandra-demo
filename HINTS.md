# Download dataset
https://www.google.com/url?q=https://www.kaggle.com/PromptCloudHQ/all-jc-penny-products&sa=D&ust=1612018862818000&usg=AOvVaw0H4wYq0MtakXTIDbWT5-4X
And copy the loaded CSV file to be able later attach it to a docker container (e.g. copy the file to the `/tmp` directort).  

# Run local Cassandra in Docker container
```
mkdir -p /tmp/cassandra-data
docker run --name cassandra-local \
  -v /tmp/cassandra-data:/var/lib/cassandra \
  -p 9042:9042 \
  -d cassandra
```

# Run interactive shell in the Cassandra container
```
docker exec -it cassandra-local bash
```
Then execute `cqlsh` to connect to the local instance

The CSV has the following head:
```
uniq_id,sku,name_title,description,list_price,sale_price,category,category_tree,average_product_rating,product_url,product_image_urls,brand,total_number_reviews,reviews
```

# Create keyspace
```
CREATE KEYSPACE IF NOT EXISTS capstone_inmemory_1 
WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };
```

# Create table
```
CREATE TABLE capstone_inmemory_1.jcpenney_products ( 
uniq_id UUID PRIMARY KEY,
sku text,
name_title text,
description text,
list_price decimal,
sale_price decimal,
category text,
category_tree text,
average_product_rating text,
product_url text,
product_image_urls text,
brand text,
total_number_reviews int,
reviews text);
```

# Load CSV into the table
```
COPY capstone_inmemory_1.jcpenney_products (uniq_id,sku,name_title,description,list_price,sale_price,category,category_tree,average_product_rating,product_url,product_image_urls,brand,total_number_reviews,reviews) 
FROM '/tmp/data/jcpenney_com-ecommerce_sample.csv' 
WITH DELIMITER=',' AND HEADER=TRUE AND ESCAPE='"';
```

# Check loaded data
```
select * from capstone_inmemory_1.jcpenney_products where uniq_id = b6c0b6be-a69c-7229-3958-5baeac73c13d;
```

# Read the documentation: Apache Cassandra Acceleration With Apache Ignite
https://ignite.apache.org/docs/latest/extensions-and-integrations/cassandra/overview

# Try local Apache Ignite in Docker container
Expose the following ports:
* 10800 - to allow thin client connections
* 11211 - for connect by internal http protocol to the TCP server
* 47100 - TcpCommunicationSpi
* 47500 - TcpDiscoverySp
* 48100 - sharedMemoryPort
* 49128 - Remote Management
```
docker run --name ignite-local \
  -v /tmp/data:/storage \
  -e IGNITE_WORK_DIR=/storage \
  -p 10800:10800 \
  -p 11211:11211 \
  -p 47100:47100 \
  -p 47500:47500 \
  -p 48100:48100 \
  -p 49128:49128 \
  -d apacheignite/ignite
```