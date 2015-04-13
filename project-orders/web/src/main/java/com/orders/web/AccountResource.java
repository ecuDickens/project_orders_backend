package com.orders.web;

import com.google.common.base.Strings;
import com.orders.exception.HttpException;
import com.orders.matchers.EmailMatcher;
import com.orders.types.ErrorType;
import com.google.inject.Inject;
import com.orders.entity.Account;
import com.orders.helper.JpaHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/profiles")
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
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorType(error))
                    .build();
        }

        final Long profileId = jpaHelper.createAccount(account);
        if (null == profileId) {
            return Response.noContent()
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorType("Unable to create account"))
                    .build();
        }

        return Response.ok(new Account().withId(profileId)).build();
    }


    @GET
    @Path("/{account_id}")
    public Response getAccount(@PathParam("account_id") final Long accountId) throws HttpException {
        final Account account = jpaHelper.getAccount(accountId);
        if (null == account) {
            return Response.noContent()
                    .status(Response.Status.NOT_FOUND)
                    .entity(new ErrorType("Account not found"))
                    .build();
        }
        return Response.ok(account).build();
    }

    @POST
    @Path("/{account_id}")
    public Response updateAccount(@PathParam("account_id") final Long profileId, final Account account) throws HttpException {
        final Account updatedAccount = jpaHelper.updateAccount(account.withId(profileId));

        return Response.ok(updatedAccount).build();
    }
}
