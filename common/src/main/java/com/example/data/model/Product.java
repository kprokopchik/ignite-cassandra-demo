package com.example.data.model;

import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Stack;
import java.util.UUID;

@Data
@Table("jcpenney_products")
public class Product {

    @Id
    @PrimaryKey("uniq_id")
    @QuerySqlField(name = "uniq_id")
    private UUID uniqId;

    @Column("sku")
    @QuerySqlField(name = "sku")
    private String sku;

    @Column("name_title")
    @QuerySqlField(name = "name_title")
    private String nameTitle;

    @Column("description")
    @QuerySqlField(name = "description")
    private String description;

    @Column("list_price")
    @QuerySqlField(name = "list_price")
    private BigDecimal listPrice;

    @Column("sale_price")
    @QuerySqlField(name = "sale_price")
    private BigDecimal salePrice;

    @Column("category")
    @QuerySqlField(name = "category")
    private String category;

    @Column("category_tree")
    @QuerySqlField(name = "category_tree")
    private String categoryTree;

    @Column("average_product_rating")
    @QuerySqlField(name = "average_product_rating")
    private String averageProductRating;

    @Column("product_url")
    @QuerySqlField(name = "product_url")
    private String productUrl;

    @Column("product_image_urls")
    @QuerySqlField(name = "product_image_urls")
    private String productImageUrls;

    @Column("brand")
    @QuerySqlField(name = "brand")
    private String brand;

    @Column("total_number_reviews")
    @QuerySqlField(name = "total_number_reviews")
    private Integer totalNumberReviews;

    @Column("reviews")
    @QuerySqlField(name = "reviews")
    private String reviews;
}
