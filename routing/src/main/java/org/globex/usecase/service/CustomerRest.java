package org.globex.usecase.service;

import org.globex.Account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/customer")
public interface CustomerRest {
	
	@GET
	@Path("/enrich")
	String get();

    @POST
    @Path("/enrich")
    @Consumes("application/json") 
    @Produces("application/json")
    Account enrich(Account customer);

}
