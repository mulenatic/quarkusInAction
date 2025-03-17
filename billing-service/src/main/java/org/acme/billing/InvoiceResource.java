package org.acme.billing;

import java.util.List;

import org.acme.billing.model.Invoice;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/invoice")
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceResource {

    @GET
    public List<Invoice> getAll() {
        return Invoice.<Invoice>listAll();
    }

}
