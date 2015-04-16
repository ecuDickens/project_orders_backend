package com.orders.web;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orders.base.ThrowingFunction1;
import com.orders.entity.*;
import com.orders.exception.HttpException;
import com.orders.helper.JpaHelper;
import com.orders.matchers.EmailMatcher;
import com.orders.types.ErrorType;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.orders.collect.MoreIterables.asFluent;
import static com.orders.entity.EntityValues.Order.Status.BILLED;
import static com.orders.entity.EntityValues.Order.Status.NEW;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/accounts")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AccountResource {

    private final JpaHelper jpaHelper;
    private final EmailMatcher emailMatcher;

    @Inject
    public AccountResource(JpaHelper jpaHelper, EmailMatcher emailMatcher) {
        this.jpaHelper = jpaHelper;
        this.emailMatcher = emailMatcher;
    }

    @POST
    public Response createAccount(final Account account) throws HttpException {

        String error = "";
        if (Strings.isNullOrEmpty(account.getFirstName()) || Strings.isNullOrEmpty(account.getLastName())) {
            error = "First and Last Name required";
        } else if (Strings.isNullOrEmpty(account.getEmail()) || !emailMatcher.validate(account.getEmail())) {
            error = "Valid email address required";
        }

        if (!Strings.isNullOrEmpty(error)) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType(error))
                    .build();
        }

        final Long accountId = jpaHelper.executeJpaTransaction(new ThrowingFunction1<Long, EntityManager, HttpException>() {
            @Override
            public Long apply(EntityManager em) throws HttpException {
                em.persist(account);
                em.flush();
                return account.getId();
            }
        });

        if (null == accountId) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType("Unable to create account"))
                    .build();
        }

        return Response.ok(new Account().withId(accountId)).build();
    }


    @GET
    @Path("/{account_id}")
    public Response getAccount(@PathParam("account_id") final Long accountId) throws HttpException {
        final DateTime now = DateTime.now();
        final Account account = jpaHelper.executeJpa(new ThrowingFunction1<Account, EntityManager, HttpException>() {
            @Override
            public Account apply(EntityManager em) throws HttpException {
                return em.find(Account.class, accountId);
            }
        });
        if (null == account) {
            return Response.noContent()
                    .entity(new ErrorType("Account not found"))
                    .build();
        }
        account.clean();
        final DateTime then = DateTime.now();
        final long millis =  then.getMillis() - now.getMillis();   // woohoo, 26 milliseconds.
        System.out.println(millis);
        return Response.ok(account).build();
    }

    @POST
    @Path("/{account_id}")
    public Response updateAccount(@PathParam("account_id") final Long accountId, final Account account) throws HttpException {
        if (!Strings.isNullOrEmpty(account.getEmail()) && !emailMatcher.validate(account.getEmail())) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType("Invalid email address for update."))
                    .build();
        }
        final Account forUpdate = jpaHelper.executeJpaTransaction(new ThrowingFunction1<Account, EntityManager, HttpException>() {
            @Override
            public Account apply(EntityManager em) throws HttpException {
                final Account forUpdate = em.find(Account.class, accountId);
                em.refresh(forUpdate, PESSIMISTIC_WRITE);
                forUpdate
                        .withEmail(Strings.isNullOrEmpty(account.getEmail()) ? forUpdate.getEmail() : account.getEmail())
                        .withFirstName(Strings.isNullOrEmpty(account.getFirstName()) ? forUpdate.getFirstName() : account.getFirstName())
                        .withLastName(Strings.isNullOrEmpty(account.getLastName()) ? forUpdate.getLastName() : account.getLastName())
                        .withAddress1(Strings.isNullOrEmpty(account.getAddress1()) ? forUpdate.getAddress1() : account.getAddress1())
                        .withAddress2(Strings.isNullOrEmpty(account.getAddress2()) ? forUpdate.getAddress2() : account.getAddress2())
                        .withCity(Strings.isNullOrEmpty(account.getCity()) ? forUpdate.getCity() : account.getCity())
                        .withState(Strings.isNullOrEmpty(account.getState()) ? forUpdate.getState() : account.getState())
                        .withPostalCode(Strings.isNullOrEmpty(account.getPostalCode()) ? forUpdate.getPostalCode() : account.getPostalCode());
                return forUpdate;
            }
        });
        if (null != forUpdate) {
            forUpdate.clean();
        }
        return Response.ok(forUpdate).build();
    }

    @POST
    @Path("/{account_id}/bill")
    public Response billAccount(@PathParam("account_id") final Long accountId) throws HttpException {
        final Invoice invoice = jpaHelper.executeJpaTransaction(new ThrowingFunction1<Invoice, EntityManager, HttpException>() {
            @Override
            public Invoice apply(EntityManager em) throws HttpException {
                final Account account = em.find(Account.class, accountId);

                // Determine the invoice total.
                Integer total = 0;
                boolean hasBillableItems = false;
                for (Order order : asFluent(account.getOrders())) {
                    if (NEW.equals(order.getStatus())) {
                        hasBillableItems = true;
                        for (OrderItem orderItem : asFluent(order.getOrderItems())) {
                            total += orderItem.getTotal();
                        }
                    }
                }

                if (!hasBillableItems) {
                    return null;
                }

                // Create the invoice and any credits/payments.
                final Invoice invoice = new Invoice()
                        .withAccount(account)
                        .withTotal(total);

                if (0 < total) {
                    final Integer accountCredit = account.getCreditBalance();
                    if (0 < accountCredit) {
                        final Credit credit = new Credit()
                                .withAccount(account)
                                .withInvoice(invoice)
                                .withIsFromInvoiceToAccount(FALSE)
                                .withTransferAmount(accountCredit > total ? total : accountCredit);
                        invoice.setCredits(Lists.newArrayList(credit));

                        em.refresh(account, PESSIMISTIC_WRITE);
                        account.setCreditBalance(accountCredit > total ? accountCredit - total : 0);
                        total -= credit.getTransferAmount();
                    }
                    if (0 < total) {
                        final Payment payment = new Payment()
                                .withPaymentAmount(total)
                                .withInvoice(invoice);
                        invoice.setPayments(Lists.newArrayList(payment));
                    }
                } else if (0 > total) {
                    final Credit credit = new Credit()
                            .withAccount(account)
                            .withInvoice(invoice)
                            .withIsFromInvoiceToAccount(TRUE)
                            .withTransferAmount(-total);
                    invoice.setCredits(Lists.newArrayList(credit));

                    em.refresh(account, PESSIMISTIC_WRITE);
                    account.setCreditBalance(account.getCreditBalance() - total);
                }
                em.persist(invoice);
                em.flush();

                // Then update the order items with the invoice id and the orders to billed
                for (Order order : asFluent(account.getOrders())) {
                    if (NEW.equals(order.getStatus())) {
                        em.refresh(order, PESSIMISTIC_WRITE);
                        order.setStatus(BILLED);
                        for (OrderItem orderItem : asFluent(order.getOrderItems())) {
                            em.refresh(orderItem, PESSIMISTIC_WRITE);
                            orderItem.setInvoice(invoice);
                        }
                    }
                }
                return invoice;
            }
        });

        if (null != invoice) {
            invoice.clean();
        }
        return Response.ok(invoice).build();
    }
}
