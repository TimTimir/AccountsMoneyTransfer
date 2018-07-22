package org.flomintv.accounts.money.transfer.rest;

import org.flomintv.accounts.money.transfer.model.Transfer;
import org.flomintv.accounts.money.transfer.model.User;

import javax.ws.rs.*;

@Consumes(value = "application/xml,application/json")
@Produces(value = "application/xml,application/json")
public interface RestTransferService {

    @GET
    @Path("/transfer/{id}")
    Transfer getTransfer(@PathParam("id") int transferId);

    @POST
    @Path("/transfer")
    String createTransfer(Transfer transfer);

    /*TODO*/
    @DELETE
    @Path("/transfer/{id}")
    void cancelTransfer(@PathParam("id") int transferId);


    @GET
    @Path("/user/{id}")
    User getUser(@PathParam("id") int userId);

    @GET
    @Path("/account/{id}")
    User getAccount(@PathParam("id") int userId);

}
