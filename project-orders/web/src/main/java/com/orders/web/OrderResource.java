package com.orders.web;

import com.google.inject.Inject;
import com.orders.base.ThrowingFunction1;
import com.orders.collect.MoreCollections;
import com.orders.entity.Account;
import com.orders.entity.Order;
import com.orders.entity.OrderItem;
import com.orders.entity.Product;
import com.orders.exception.HttpException;
import com.orders.helper.JpaHelper;
import com.orders.types.ErrorType;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/orders")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class OrderResource {

    private final JpaHelper jpaHelper;

    @Inject
    public OrderResource(JpaHelper jpaHelper) {
        this.jpaHelper = jpaHelper;
    }

    @POST
    public Response placeOrder(final Order order) throws HttpException {
        if (MoreCollections.isNullOrEmpty(order.getOrderItems())) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType("At least one order item is required."))
                    .build();
        }

        final Long orderId = jpaHelper.executeJpaTransaction(new ThrowingFunction1<Long, EntityManager, HttpException>() {
            @Override
            public Long apply(EntityManager em) throws HttpException {
                order.withAccount(em.getReference(Account.class, order.getAccountId()));
                for (OrderItem item : order.getOrderItems()) {
                    item.setOrder(order);
                    item.setProduct(em.getReference(Product.class, item.getProductSku()));
                }
                em.persist(order);
                em.flush();
                return order.getId();
            }
        });
        if (null == orderId) {
            return Response.noContent()
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorType("Unable to place order."))
                    .build();
        }

        return Response.ok(new Account().withId(orderId)).build();
    }

    @GET
    @Path("/{order_id}")
    public Response getOrder(@PathParam("order_id") final Long orderid) throws HttpException {
        final Order order = jpaHelper.executeJpa(new ThrowingFunction1<Order, EntityManager, HttpException>() {
            @Override
            public Order apply(EntityManager em) throws HttpException {
                return em.find(Order.class, orderid);
            }
        });
        if (null == order) {
            return Response.noContent()
                    .entity(new ErrorType("Order not found"))
                    .build();
        }
        order.clean();
        return Response.ok(order).build();
    }
}
