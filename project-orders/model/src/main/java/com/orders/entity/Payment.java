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
 * Represents a payment made against an invoice.
 */
@Entity
@Table(name = "PAYMENTS")
@JsonSerialize(include = NON_NULL)
public class Payment {
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
    @JoinColumn(name="INVOICE_ID",referencedColumnName="ID")
    private Invoice invoice;

    @Column(name = "INVOICE_ID")
    private Long invoiceId;

    @Column(name = "PAYMENT_AMOUNT", nullable = false)
    private Integer paymentAmount;

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

    public Invoice getInvoice() {
        return invoice;
    }
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Integer getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(Integer paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Payment withId(final Long id) {
        setId(id);
        return this;
    }
    public Payment withCreatedDatetime(final Timestamp createdDatetime) {
        setCreatedDatetime(createdDatetime);
        return this;
    }
    public Payment withLastModifiedDatetime(final Timestamp lastModifiedDatetime) {
        setLastModifiedDatetime(lastModifiedDatetime);
        return this;
    }
    public Payment withInvoice(final Invoice invoice) {
        setInvoice(invoice);
        return this;
    }
    public Payment withInvoiceId(final Long invoiceId) {
        setInvoiceId(invoiceId);
        return this;
    }
    public Payment withPaymentAmount(final Integer paymentAmount) {
        setPaymentAmount(paymentAmount);
        return this;
    }

    @JsonIgnore
    public void clean() {
        invoice = null;
    }
}
