#!/usr/bin/env bash

cqlsh cassandra 9042 -f /prepare-keyspace/prepare-keyspace.cql

FILE_URL="https://gist.githubusercontent.com/kprokopchik/6b08cc31f715bb21bab38866af83b132/raw/36c6ea9308f3664d323d9dc6800c049ad77ea6f4/jcpenney_com-ecommerce_sample.csv"

curl $FILE_URL > /tmp/jcpenney_com-ecommerce_sample.csv

echo data loaded: `ls -la /tmp/jcpenney_com-ecommerce_sample.csv`

cqlsh cassandra 9042 -k capstone_inmemory_1 -e "COPY jcpenney_products
(uniq_id,sku,name_title,description,list_price,sale_price,category,category_tree,average_product_rating,product_url,product_image_urls,brand,total_number_reviews,reviews)
FROM '/tmp/jcpenney_com-ecommerce_sample.csv'
WITH DELIMITER=',' AND HEADER=TRUE AND ESCAPE='\"';"

echo Import done. RC=$?