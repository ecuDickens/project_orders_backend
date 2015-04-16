package com.orders.web;

import com.google.inject.Inject;
import com.orders.base.ThrowingFunction1;
import com.orders.entity.Invoice;
import com.orders.exception.HttpException;
import com.orders.helper.JpaHelper;
import com.orders.types.ErrorType;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/invoices")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class InvoiceResource {

    private final JpaHelper jpaHelper;

    @Inject
    public InvoiceResource(JpaHelper jpaHelper) {
        this.jpaHelper = jpaHelper;
    }

    @GET
    @Path("/{invoice_id}")
    public Response getInvoice(@PathParam("invoice_id") final Long invoiceId) throws HttpException {
        final Invoice invoice = jpaHelper.executeJpa(new ThrowingFunction1<Invoice, EntityManager, HttpException>() {
            @Override
            public Invoice apply(EntityManager em) throws HttpException {
                return em.find(Invoice.class, invoiceId);
            }
        });
        if (null == invoice) {
            return Response.noContent()
                    .entity(new ErrorType("Account not found"))
                    .build();
        }
        invoice.clean();
        return Response.ok(invoice).build();
    }
}
