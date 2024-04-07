package fr.epita.assistants.jws.presentation.rest;

import fr.epita.assistants.jws.domain.service.GameService;
import fr.epita.assistants.jws.presentation.rest.request.CreateGameRequest;
import fr.epita.assistants.jws.presentation.rest.request.MovePlayerRequest;
import fr.epita.assistants.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistants.jws.presentation.rest.response.GetAllGameResponse;
import fr.epita.assistants.jws.utils.State;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.annotations.Pos;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)

public class Endpoint {

    @Inject
    GameService gameService;

    @Path("games")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    public Response GetAllGame() {
        List<GetAllGameResponse> infoList = gameService.createInfoList();
        return Response
                .ok()
                .entity(infoList)
                .status(Response.Status.OK)
                .build();


    }

    @Path("games")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response CreateGame(@RequestBody CreateGameRequest request) {
        if (request == null || request.name == null || request.name.isEmpty()) {
            return Response
                    .status(400)
                    .build();
        }
        GameDetailResponse toRet = gameService.CreateGame(request.name);
        return Response
                .ok()
                .entity(toRet)
                .status(Response.Status.OK)
                .build();
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("games/{game_id}")

    public Response GetGameInfo(@PathParam("game_id") String game_id) {
        GameDetailResponse toRet = gameService.getGameInfo(game_id);
        if (game_id == null || game_id.isEmpty() || toRet == null) {
            return Response
                    .status(404)
                    .build();
        }
        return Response
                .ok()
                .entity(toRet)
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("games/{gameId}")
    public Response JoinAGame(@PathParam("gameId") String gameId, @RequestBody CreateGameRequest request) {
        if (gameService.getPlayersCount(Integer.valueOf(gameId)) >= 4 || gameId == null || gameId.isEmpty() || request == null || request.name == null || request.name.isEmpty()) {
            return Response
                    .status(400)
                    .build();
        }
        GameDetailResponse toRet = gameService.joinGame(gameId, request.name);
        if (toRet == null) {
            return Response
                    .status(404)
                    .build();
        }
        //todo do finished thing
        if (toRet.state != State.STARTING) {
            return Response
                    .status(400)
                    .build();

        }
        return Response
                .ok()
                .entity(toRet)
                .status(Response.Status.OK)
                .build();
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("games/{gameId}/start")

    public Response start_game(@PathParam("gameId") String gameId) {
        if (gameId == null || gameId.isEmpty()) {
            return Response
                    .status(404)
                    .build();
        }
        GameDetailResponse toret = gameService.StartGame(gameId);
        if (toret == null) {
            return Response
                    .status(404)
                    .build();
        }
        return Response
                .ok()
                .entity(toret)
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/games/{gameId}/players/{playersId}/move")
    public Response move_player(@PathParam("gameId") String gameId, @PathParam("playersId") String playerId,
                                @RequestBody() MovePlayerRequest movePlayerRequest) {
        System.out.println("game id " + gameId);
        System.out.println("player id " + playerId);
        System.out.println("posx " + movePlayerRequest.posX);
        System.out.println("posy " + movePlayerRequest.posY);
        if (gameId == null || gameId.isEmpty() || playerId == null || playerId.isEmpty() || movePlayerRequest == null) {
            System.out.println("something is null");
            return Response
                    .status(400)
                    .build();
        }
        GameDetailResponse toRet = gameService.MovePlayer(gameId, playerId, movePlayerRequest.posX,
                movePlayerRequest.posY);
        if (toRet == null) {
            System.out.println("toret is null");
            return Response
                    .status(400)
                    .build();
        }
        System.out.println();
        if (toRet.map.get(0).equals("nofound")) {
            System.out.println("no found");
            return Response
                    .status(404)
                    .build();
        }
        if (toRet.map.get(0).equals("notime")) {
            System.out.println("no time ");
            return Response
                    .status(429)
                    .build();
        }
        System.out.println("normal resp");
        return Response
                .ok()
                .entity(toRet)
                .status(Response.Status.OK)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/games/{gameId}/players/{playersId}/bomb")
    public Response PlaceBomb(@PathParam("gameId") String gameId, @PathParam("playersId") String playerId,
                                @RequestBody() MovePlayerRequest movePlayerRequest)
    {
        if (gameId == null || gameId.isEmpty() || playerId == null || playerId.isEmpty() || movePlayerRequest == null) {
            System.out.println("something is null");
            return Response
                    .status(400)
                    .build();
        }
        GameDetailResponse toRet = gameService.placeBomb(gameId, playerId, movePlayerRequest.posX,
                movePlayerRequest.posY);
        if (toRet == null)
        {
            System.out.println("toret is null");
            return Response
                    .status(429)
                    .build();
        }

        if (toRet.map.get(0).equals("400"))
        {
            System.out.println("error 400");
            return Response
                    .status(400)
                    .build();
        }
        if (toRet.map.get(0).equals("404"))
        {
            System.out.println("eror 404");
            return Response
                    .status(404)
                    .build();
        }
        System.out.println("normal rep");
        return Response
                .ok()
                .entity(toRet)
                .status(Response.Status.OK)
                .build();

    }

}
