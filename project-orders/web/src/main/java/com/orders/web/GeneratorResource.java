package com.orders.web;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orders.base.ThrowingFunction1;
import com.orders.entity.*;
import com.orders.exception.HttpException;
import com.orders.helper.JpaHelper;
import com.orders.matchers.EmailMatcher;
import com.orders.types.ErrorType;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.orders.collect.MoreIterables.asFluent;
import static com.orders.entity.EntityValues.Order.Status.BILLED;
import static com.orders.entity.EntityValues.Product.Type.ONE_TIME;
import static java.lang.Boolean.TRUE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/generate")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class GeneratorResource {

    private final JpaHelper jpaHelper;
    private final EmailMatcher emailMatcher;

    @Inject
    public GeneratorResource(JpaHelper jpaHelper, EmailMatcher emailMatcher) {
        this.jpaHelper = jpaHelper;
        this.emailMatcher = emailMatcher;
    }


    @POST
    @Path("/{email}/{num_to_generate}")
    public Response updateAccount(@PathParam("email") final String email, @PathParam("num_to_generate") final Integer numToGenerate) throws HttpException {
        // Validate
        if (Strings.isNullOrEmpty(email) && !emailMatcher.validate(email)) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType("Invalid email address."))
                    .build();
        }
        final Set<Long> accountIds = jpaHelper.executeJpaTransaction(new ThrowingFunction1<Set<Long>, EntityManager, HttpException>() {
            @Override
            public Set<Long> apply(EntityManager em) throws HttpException {
                final List<Account> accounts = Lists.newArrayListWithExpectedSize(numToGenerate);
                final List<Product> products = em.createQuery("SELECT a FROM Product a", Product.class).getResultList();
                for (int i = 0; i < numToGenerate; i++) {
                    final Account account = new Account()
                            .withEmail(i + email)
                            .withFirstName("Test")
                            .withLastName("Account"+i)
                            .withAddress1("1001 East 5th Street")
                            .withCity("Greenville")
                            .withState("NC")
                            .withPostalCode("27858")
                            .withCreditBalance(0)
                            .withInvoices(Lists.<Invoice>newArrayList())
                            .withOrders(Lists.<Order>newArrayList())
                            .withCredits(Lists.<Credit>newArrayList());

                    final Invoice invoice = new Invoice()
                            .withAccount(account)
                            .withPayments(Lists.<Payment>newArrayList())
                            .withCredits(Lists.<Credit>newArrayList());
                    Integer invoiceTotal = 0;
                    for (int o = 0; o < getRandom(1, 5); o++) {
                        final Order order = new Order()
                                .withAccount(account)
                                .withStatus(BILLED)
                                .withOrderItems(Lists.<OrderItem>newArrayList());
                        for (int oi = 0; oi < getRandom(1, 5); oi++) {
                            final Product product = products.get(getRandom(0, 3));
                            final OrderItem item = new OrderItem()
                                    .withOrder(order)
                                    .withInvoice(invoice)
                                    .withProduct(em.getReference(Product.class, product.getSku()))
                                    .withQuantity(getRandom(1, 4))
                                    .withPrice(ONE_TIME.equals(product.getType()) ? product.getListPrice() : -(getRandom(1, 20) * 100));
                            invoiceTotal += item.getPrice() * item.getQuantity();
                            order.getOrderItems().add(item);
                        }
                        account.getOrders().add(order);
                    }
                    invoice.setTotal(invoiceTotal);
                    if (0 < invoiceTotal) {
                        final Payment payment = new Payment()
                                .withInvoice(invoice)
                                .withPaymentAmount(invoiceTotal);
                        invoice.getPayments().add(payment);
                    } else if (0 > invoiceTotal) {
                        final Credit credit = new Credit()
                                .withAccount(account)
                                .withInvoice(invoice)
                                .withTransferAmount(-invoiceTotal)
                                .withIsFromInvoiceToAccount(TRUE);
                        invoice.getCredits().add(credit);
                        account.setCreditBalance(-invoiceTotal);
                    }
                    account.getInvoices().add(invoice);

                    em.persist(account);
                    accounts.add(account);
                }
                em.flush();

                return asFluent(accounts).map(new Function<Account, Long>() {
                    @Override
                    public Long apply(final Account account) {
                        return account.getId();
                    }
                }).toSet();
            }
        });
        return Response.ok(accountIds).build();
    }

    public static int getRandom(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
