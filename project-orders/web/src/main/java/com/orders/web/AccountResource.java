package com.orders.web;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.orders.base.ThrowingFunction1;
import com.orders.entity.Account;
import com.orders.exception.HttpException;
import com.orders.helper.JpaHelper;
import com.orders.matchers.EmailMatcher;
import com.orders.types.ErrorType;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;

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

        final Long profileId = jpaHelper.executeJpaTransaction(new ThrowingFunction1<Long, EntityManager, HttpException>() {
            @Override
            public Long apply(EntityManager em) throws HttpException {
                em.persist(account);
                em.flush();
                return account.getId();
            }
        });

        if (null == profileId) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType("Unable to create account"))
                    .build();
        }

        return Response.ok(new Account().withId(profileId)).build();
    }


    @GET
    @Path("/{account_id}")
    public Response getAccount(@PathParam("account_id") final Long accountId) throws HttpException {
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
        return Response.ok(account).build();
    }

    @POST
    @Path("/{account_id}")
    public Response updateAccount(@PathParam("account_id") final Long accountId, final Account account) throws HttpException {
        // Validate
        if (!Strings.isNullOrEmpty(account.getEmail()) && !emailMatcher.validate(account.getEmail())) {
            return Response
                    .status(BAD_REQUEST)
                    .entity(new ErrorType("Invalid email address for update."))
                    .build();
        }
        final Account forUpdate = jpaHelper.executeJpaTransaction(new ThrowingFunction1<Account, EntityManager, HttpException>() {
            @Override
            public Account apply(EntityManager em) throws HttpException {
                final Account accountForUpdate = em.find(Account.class, accountId);
                em.refresh(accountForUpdate, LockModeType.PESSIMISTIC_WRITE);
                accountForUpdate
                        .withEmail(Strings.isNullOrEmpty(account.getEmail()) ? accountForUpdate.getEmail() : account.getEmail())
                        .withFirstName(Strings.isNullOrEmpty(account.getFirstName()) ? accountForUpdate.getFirstName() : account.getFirstName())
                        .withLastName(Strings.isNullOrEmpty(account.getLastName()) ? accountForUpdate.getLastName() : account.getLastName())
                        .withLastModifiedDatetime(new Timestamp(DateTime.now().getMillis()));
                return account;
            }
        });

        return Response.ok(forUpdate).build();
    }
}
