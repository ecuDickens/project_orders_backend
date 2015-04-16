package com.orders.entity;

import com.orders.datetime.TimestampDeserializer;
import com.orders.datetime.TimestampSerializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.sql.Timestamp;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

/**
 * Represents an order for an order.  When the order is billed an invoice is generated that the order item is
 * tied to as well.
 */
@Entity
@Table(name = "ORDER_ITEMS")
@JsonSerialize(include = NON_NULL)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "CREATED_DATETIME", nullable = false)
    @JsonSerialize(using=TimestampSerializer.class, include = NON_NULL)
    @JsonDeserialize(using=TimestampDeserializer.class)
    private Timestamp createdDatetime;

    @Column(name = "LAST_MODIFIED_DATETIME", nullable = false)
    @JsonSerialize(using=TimestampSerializer.class, include = NON_NULL)
    @JsonDeserialize(using=TimestampDeserializer.class)
    private Timestamp lastModifiedDatetime;

    @ManyToOne(optional = false)
    @JoinColumn(name="ORDER_ID", referencedColumnName="ID")
    private Order order;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @ManyToOne(optional = false)
    @JoinColumn(name="SKU", referencedColumnName="SKU")
    private Product product;

    @Column(name = "SKU")
    private String productSku;

    @ManyToOne(optional = true)
    @JoinColumn(name="INVOICE_ID", referencedColumnName="ID")
    private Invoice invoice;

    @Column(name = "INVOICE_ID")
    private Long invoiceId;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "EFFECTIVE_PRICE")
    private Integer price;

    @Column(name = "TOTAL", nullable = false)
    private Integer total;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        createdDatetime = new Timestamp(DateTime.now().getMillis());
        lastModifiedDatetime = new Timestamp(DateTime.now().getMillis());
        total = quantity * price;
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

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public Invoice getInvoice() {
        return invoice;
    }
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getProductSku() {
        return productSku;
    }
    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getTotal() {
        return total;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }

    public OrderItem withId(final Long id) {
        setId(id);
        return this;
    }
    public OrderItem withCreatedDatetime(final Timestamp createdDatetime) {
        setCreatedDatetime(createdDatetime);
        return this;
    }
    public OrderItem withLastModifiedDatetime(final Timestamp lastModifiedDatetime) {
        setLastModifiedDatetime(lastModifiedDatetime);
        return this;
    }
    public OrderItem withOrder(final Order order) {
        setOrder(order);
        return this;
    }
    public OrderItem withProduct(final Product product) {
        setProduct(product);
        return this;
    }
    public OrderItem withInvoice(final Invoice invoice) {
        setInvoice(invoice);
        return this;
    }
    public OrderItem withInvoiceId(final Long invoiceId) {
        setInvoiceId(invoiceId);
        return this;
    }
    public OrderItem withProductSku(final String productSku) {
        setProductSku(productSku);
        return this;
    }
    public OrderItem withOrderId(final Long orderId) {
        setOrderId(orderId);
        return this;
    }
    public OrderItem withQuantity(final Integer quantity) {
        setQuantity(quantity);
        return this;
    }
    public OrderItem withPrice(final Integer price) {
        setPrice(price);
        return this;
    }
    public OrderItem withTotal(final Integer total) {
        setTotal(total);
        return this;
    }

    @JsonIgnore
    public void clean() {
        order = null;
        invoice = null;
        if (null != product) {
            product.clean();
            productSku = null;
        }
    }
}
