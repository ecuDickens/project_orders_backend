package com.orders.web;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.orders.base.ThrowingFunction1;
import com.orders.collect.MoreCollections;
import com.orders.entity.Product;
import com.orders.exception.HttpException;
import com.orders.helper.JpaHelper;
import com.orders.types.ErrorType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.orders.collect.MoreIterables.asFluent;
import static com.orders.entity.EntityValues.Product.Type.VALID_TYPES;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/products")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ProductResource {

    private final JpaHelper jpaHelper;

    @Inject
    public ProductResource(JpaHelper jpaHelper) {
        this.jpaHelper = jpaHelper;
    }

    @POST
    public Response createProduct(final Product product) throws HttpException {
        if (Strings.isNullOrEmpty(product.getSku())) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType("Sku is required."))
                    .build();
        }
        if (Strings.isNullOrEmpty(product.getType()) || !VALID_TYPES.contains(product.getType())) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType("Product Type must be one of: " +VALID_TYPES))
                    .build();
        }

        final String sku = jpaHelper.executeJpaTransaction(new ThrowingFunction1<String, EntityManager, HttpException>() {
            @Override
            public String apply(EntityManager em) throws HttpException {
                em.persist(product);
                em.flush();
                return product.getSku();
            }
        });

        return Response.ok(new Product().withSku(sku)).build();
    }

    @GET
    public Response getProducts() throws HttpException {
        final List<Product> products = jpaHelper.executeJpa(new ThrowingFunction1<List<Product>, EntityManager, HttpException>() {
            @Override
            public List<Product> apply(EntityManager em) throws HttpException {
                TypedQuery<Product> query = em.createQuery("SELECT a FROM Product a", Product.class);
                return query.getResultList();
            }
        });
        if (MoreCollections.isNullOrEmpty(products)) {
            return Response.noContent()
                    .entity(new ErrorType("No products found"))
                    .build();
        }
        for (Product product : asFluent(products)) {
            product.clean();
        }
        return Response.ok(products).build();
    }

    @GET
    @Path("/{sku}")
    public Response getProduct(@PathParam("sku") final String sku) throws HttpException {
        final Product product = jpaHelper.executeJpa(new ThrowingFunction1<Product, EntityManager, HttpException>() {
            @Override
            public Product apply(EntityManager em) throws HttpException {
                return em.find(Product.class, sku);
            }
        });
        if (null == product) {
            return Response.noContent()
                    .entity(new ErrorType("Account not found"))
                    .build();
        }
        product.clean();
        return Response.ok(product).build();
    }
}
