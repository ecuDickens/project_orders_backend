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

import static com.orders.collect.MoreIterables.asFluent;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

/**
 * Represents an invoice for a collection of order items belonging to the same account (could span multiple orders).
 */
@Entity
@Table(name = "INVOICES")
@JsonSerialize(include = NON_NULL)
public class Invoice {
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
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    private Account account;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "TOTAL", nullable = false)
    private Integer total;

    @OneToMany(mappedBy = "invoice", cascade = ALL, fetch = EAGER)
    private List<OrderItem> orderItems;

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

    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getAccountId() {
        return accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getTotal() {
        return total;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Invoice withId(final Long id) {
        setId(id);
        return this;
    }
    public Invoice withCreatedDatetime(final Timestamp createdDatetime) {
        setCreatedDatetime(createdDatetime);
        return this;
    }
    public Invoice withLastModifiedDatetime(final Timestamp lastModifiedDatetime) {
        setLastModifiedDatetime(lastModifiedDatetime);
        return this;
    }
    public Invoice withAccount(final Account account) {
        setAccount(account);
        return this;
    }
    public Invoice withAccountId(final Long accountId) {
        setAccountId(accountId);
        return this;
    }
    public Invoice withTotal(final Integer total) {
        setTotal(total);
        return this;
    }
    public Invoice withOrderItems(final List<OrderItem> orderItems) {
        setOrderItems(orderItems);
        return this;
    }

    @JsonIgnore
    public void clean() {
        account = null;
        for (OrderItem orderItem : asFluent(orderItems)) {
            orderItem.clean();
        }
    }
}
