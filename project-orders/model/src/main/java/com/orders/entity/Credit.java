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
 * Represents a credit transferring a negative invoice balance to the account or transferring an account credit to a
 * positive invoice.
 */
@Entity
@Table(name = "CREDIT")
@JsonSerialize(include = NON_NULL)
public class Credit {
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

    @ManyToOne(optional = false)
    @JoinColumn(name="ACCOUNT_ID",referencedColumnName="ID")
    private Account account;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "IS_FROM_INVOICE_TO_ACCOUNT", nullable = false)
    private Boolean isFromInvoiceToAccount;

    @Column(name = "TRANSFER_AMOUNT", nullable = false)
    private Integer transferAmount;

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

    public Long getInvoiceId() {
        return invoiceId;
    }
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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

    public Boolean getIsFromInvoiceToAccount() {
        return isFromInvoiceToAccount;
    }
    public void setIsFromInvoiceToAccount(Boolean isFromInvoiceToAccount) {
        this.isFromInvoiceToAccount = isFromInvoiceToAccount;
    }

    public Integer getTransferAmount() {
        return transferAmount;
    }
    public void setTransferAmount(Integer transferAmount) {
        this.transferAmount = transferAmount;
    }

    public Credit withId(final Long id) {
        setId(id);
        return this;
    }
    public Credit withCreatedDatetime(final Timestamp createdDatetime) {
        setCreatedDatetime(createdDatetime);
        return this;
    }
    public Credit withLastModifiedDatetime(final Timestamp lastModifiedDatetime) {
        setLastModifiedDatetime(lastModifiedDatetime);
        return this;
    }
    public Credit withInvoice(final Invoice invoice) {
        setInvoice(invoice);
        return this;
    }
    public Credit withInvoiceId(final Long invoiceId) {
        setInvoiceId(invoiceId);
        return this;
    }
    public Credit withAccount(final Account account) {
        setAccount(account);
        return this;
    }
    public Credit withAccountId(final Long accountId) {
        setAccountId(accountId);
        return this;
    }
    public Credit withIsFromInvoiceToAccount(final Boolean isFromInvoiceToAccount) {
        setIsFromInvoiceToAccount(isFromInvoiceToAccount);
        return this;
    }
    public Credit withTransferAmount(final Integer transferAmount) {
        setTransferAmount(transferAmount);
        return this;
    }

    @JsonIgnore
    public void clean() {
        account = null;
        invoice = null;
    }
}
