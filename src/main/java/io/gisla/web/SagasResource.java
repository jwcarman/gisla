package io.gisla.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/sagas")
public interface SagasResource {

    @POST
    @Path("")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    CreateSagaResponse createSaga(CreateSagaRequest request);
}
