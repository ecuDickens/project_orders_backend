package com.orders.entity;

import com.orders.datetime.TimestampDeserializer;
import com.orders.datetime.TimestampSerializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import static com.orders.collect.MoreIterables.asFluent;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

/**
 * Represents a customer's account and provides a central point to retrieve
 * all orders and invoices.
 */
@Entity
@Table(name = "ACCOUNTS")
@JsonSerialize(include = NON_NULL)
public class Account implements Serializable {
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

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "FIRST_NAME", length = 50, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 50, nullable = false)
    private String lastName;

    @Column(name = "ADDRESS1", length = 50, nullable = false)
    private String address1;

    @Column(name = "ADDRESS2", length = 50)
    private String address2;

    @Column(name = "CITY", length = 50, nullable = false)
    private String city;

    @Column(name = "STATE", length = 2, nullable = false)
    private String state;

    @Column(name = "POSTAL_CODE", length = 10)
    private String postalCode;

    @Column(name = "CREDIT_BALANCE")
    private Integer creditBalance;

    @OneToMany(mappedBy = "account", targetEntity = Order.class, cascade = ALL, fetch = EAGER)
    private List<Order> orders;

    @OneToMany(mappedBy = "account", targetEntity = Invoice.class, cascade = ALL, fetch = EAGER)
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "account", targetEntity = Credit.class, cascade = ALL, fetch = EAGER)
    private List<Credit> credits;

    public Account() {}

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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getCreditBalance() {
        return creditBalance;
    }
    public void setCreditBalance(Integer creditBalance) {
        this.creditBalance = creditBalance;
    }

    public List<Order> getOrders() {
        return orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }
    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public List<Credit> getCredits() {
        return credits;
    }
    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }

    public Account withId(final Long id) {
        setId(id);
        return this;
    }
    public Account withCreatedDatetime(final Timestamp createdDatetime) {
        setCreatedDatetime(createdDatetime);
        return this;
    }
    public Account withLastModifiedDatetime(final Timestamp lastModifiedDatetime) {
        setLastModifiedDatetime(lastModifiedDatetime);
        return this;
    }
    public Account withEmail(final String email) {
        setEmail(email);
        return this;
    }
    public Account withLastName(final String lastName) {
        setLastName(lastName);
        return this;
    }
    public Account withFirstName(final String firstName) {
        setFirstName(firstName);
        return this;
    }
    public Account withCreditBalance(final Integer creditBalance) {
        setCreditBalance(creditBalance);
        return this;
    }
    public Account withPostalCode(final String postalCode) {
        setPostalCode(postalCode);
        return this;
    }
    public Account withState(final String state) {
        setState(state);
        return this;
    }
    public Account withCity(final String city) {
        setCity(city);
        return this;
    }
    public Account withAddress2(final String address2) {
        setAddress2(address2);
        return this;
    }
    public Account withAddress1(final String address1) {
        setAddress1(address1);
        return this;
    }
    public Account withOrders(final List<Order> orders) {
        setOrders(orders);
        return this;
    }
    public Account withInvoices(final List<Invoice> invoices) {
        setInvoices(invoices);
        return this;
    }
    public Account withCredits(final List<Credit> credits) {
        setCredits(credits);
        return this;
    }

    @JsonIgnore
    public void clean() {
        for (Order order : asFluent(orders)) {
            order.clean();
        }
        for (Invoice invoice : asFluent(invoices)) {
            invoice.clean();
        }
        for (Credit credit : asFluent(credits)) {
            credit.clean();
        }
    }
}
