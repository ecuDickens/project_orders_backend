package com.orders.entity;

import com.orders.datetime.TimestampDeserializer;
import com.orders.datetime.TimestampSerializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

/**
 * Represents a product in the product catalog.  The id is the related sku for the product.
 */
@Entity
@Table(name = "PRODUCTS")
@JsonSerialize(include = NON_NULL)
public class Product {
    @Id
    @Column(name = "SKU", length = 32)
    private String sku;

    @Column(name = "CREATED_DATETIME", nullable = false)
    @JsonSerialize(using=TimestampSerializer.class, include = NON_NULL)
    @JsonDeserialize(using=TimestampDeserializer.class)
    private Timestamp createdDatetime;

    @Column(name = "LAST_MODIFIED_DATETIME", nullable = false)
    @JsonSerialize(using=TimestampSerializer.class, include = NON_NULL)
    @JsonDeserialize(using=TimestampDeserializer.class)
    private Timestamp lastModifiedDatetime;

    @Column(name = "TYPE", nullable = false)
    private String type;   // one time charge, recurring charge, credit.

    @Column(name = "LIST_PRICE", nullable = false)
    private Integer listPrice;

    @OneToMany(mappedBy = "product", targetEntity = OrderItem.class, fetch = LAZY)
    private List<OrderItem> orderItems;

    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }

    @PrePersist
    protected void onCreate() {
        createdDatetime = new Timestamp(DateTime.now().getMillis());
        lastModifiedDatetime = new Timestamp(DateTime.now().getMillis());
        if (null == listPrice) {
            listPrice = 0;
        }
    }
    public Timestamp getCreatedDatetime() {
        return createdDatetime;
    }
    public void setCreatedDatetime(Timestamp createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDatetime = new Timestamp(DateTime.now().getMillis());
    }
    public Timestamp getLastModifiedDatetime() {
        return lastModifiedDatetime;
    }
    public void setLastModifiedDatetime(Timestamp lastModifiedDatetime) {
        this.lastModifiedDatetime = lastModifiedDatetime;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Integer getListPrice() {
        return listPrice;
    }
    public void setListPrice(Integer listPrice) {
        this.listPrice = listPrice;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Product withSku(final String sku) {
        setSku(sku);
        return this;
    }
    public Product withCreatedDatetime(final Timestamp createdDatetime) {
        setCreatedDatetime(createdDatetime);
        return this;
    }
    public Product withLastModifiedDatetime(final Timestamp lastModifiedDatetime) {
        setLastModifiedDatetime(lastModifiedDatetime);
        return this;
    }
    public Product withType(final String type) {
        setType(type);
        return this;
    }
    public Product withListPrice(final Integer listPrice) {
        setListPrice(listPrice);
        return this;
    }
    public Product withOrderItems(final List<OrderItem> orderItems) {
        setOrderItems(orderItems);
        return this;
    }

    @JsonIgnore
    public void clean() {
        orderItems = null;
    }
}
