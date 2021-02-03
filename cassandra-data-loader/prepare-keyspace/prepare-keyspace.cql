CREATE KEYSPACE IF NOT EXISTS capstone_inmemory_1
WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };

CREATE TABLE IF NOT EXISTS capstone_inmemory_1.jcpenney_products (
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
