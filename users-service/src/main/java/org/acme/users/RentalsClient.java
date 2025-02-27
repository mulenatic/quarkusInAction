package org.acme.users;

import java.util.Collection;
import java.util.List;

import org.acme.users.model.Rental;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@RegisterRestClient(baseUri = "http://localhost:8082")
@Path("rental")
public interface RentalsClient {

  @GET
  public List<Rental> getAll();


}
