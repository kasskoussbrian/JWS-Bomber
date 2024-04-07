package fr.epita.assistants.presentation.rest;


import fr.epita.assistants.presentation.rest.request.ReverseRequest;
import fr.epita.assistants.presentation.rest.response.HelloResponse;
import fr.epita.assistants.presentation.rest.response.ReverseResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.annotations.Pos;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class Endpoints {

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("hello/{name}")
    public Response SayHello(@PathParam("name") String name)
    {
        if (name== null || name.isEmpty())
        {
            throw new BadRequestException("empty or null");
        }
         return Response
                .status(Response.Status.OK)
                .entity(new HelloResponse(name))
                .build();

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("reverse")
    public Response ReverseStr(@RequestBody ReverseRequest request)
    {
        if (request == null || request.content == null || request.content.isEmpty() )
        {
            return Response
                    .status(400)
                    .build();
        }
        return Response
                .ok()
                .entity(new ReverseResponse(request.content))
                .status(Response.Status.OK)
                .build();
    }

}
