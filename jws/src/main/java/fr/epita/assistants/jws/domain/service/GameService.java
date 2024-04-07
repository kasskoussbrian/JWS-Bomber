package fr.epita.assistants.jws.domain.service;

import fr.epita.assistants.jws.converter.GameConverter;
import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.data.repository.GameRepository;
import fr.epita.assistants.jws.data.repository.PlayerRepository;
import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistants.jws.presentation.rest.response.GetAllGameResponse;
import fr.epita.assistants.jws.utils.State;
import fr.epita.assistants.jws.utils.playerUtils;
import fr.epita.assistants.jws.utils.Bombtimer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Transactional
@ApplicationScoped
public class GameService {
    @Inject
    public GameRepository gameRepository;
    @Inject
    public PlayerRepository playerRepository;

    public List<GetAllGameResponse> createInfoList() {
        List<GetAllGameResponse> toRet = new ArrayList<>();
        for (GameEntity ge : GameConverter.convertModelToEntityList(gameRepository.listAll())) {
            int count = 0;
            for (PlayerModel pm : playerRepository.listAll()) {
                if (pm.game_id == ge.id) {
                    count++;
                }
            }
            toRet.add(new GetAllGameResponse(ge.id, count, ge.state));
        }
        return toRet;
    }


    List<String> getMap() {

        List<String> toRet = new ArrayList<>();
        BufferedReader reader;

        String path = System.getenv("JWS_MAP_PATH");
        if (path == null) {
            path = "src/test/resources/map1.rle";
        }
        try {

            reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();

            while (line != null) {
                toRet.add(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toRet;
    }

    public GameDetailResponse CreateGame(String name) {


        GameModel newgame = new GameModel();
        newgame.map = getMap();
        newgame.starttime = Timestamp.valueOf(LocalDateTime.now());
        newgame.state = "STARTING";
        List<playerUtils> players = new ArrayList<>();
        gameRepository.persist(newgame);
        players.add(createPlayerAndAdd(1, 1, name, Math.toIntExact(newgame.id)));


        return new GameDetailResponse(players, newgame.map, Math.toIntExact(newgame.id), newgame.starttime,
                State.STARTING);
    }

    public playerUtils createPlayerAndAdd(int posX, int posY, String name, int gameid) {


        PlayerModel newplayer = new PlayerModel();
        newplayer.game_id = (long) gameid;
        newplayer.posX = posX;
        newplayer.posY = posY;
        newplayer.name = name;
        newplayer.lives = 3;
        playerRepository.persist(newplayer);
        playerUtils toRet = new playerUtils(name, posX, posY, Math.toIntExact(newplayer.id), 3);
        return toRet;
    }


    public GameDetailResponse getGameInfo(String id) {

        int tocmp = Integer.parseInt(id);
        for (GameEntity e : GameConverter.convertModelToEntityList(gameRepository.listAll())) {
            if (e.id == tocmp) {
                List<playerUtils> playrelist = new ArrayList<>();
                for (PlayerModel pm : playerRepository.listAll()) {
                    if (pm.game_id == e.id) {
                        playrelist.add(new playerUtils(pm.name, pm.posX, pm.posY, Math.toIntExact(pm.id), pm.lives));
                    }
                }
                return new GameDetailResponse(playrelist, e.Map.stream().toList(), Math.toIntExact(e.id), e.startTime
                        , e.state);
            }
        }
        return null;
    }

    public int getPlayersCount(Integer gameId) {

        int count = 0;
        for (PlayerModel p : playerRepository.listAll()) {
            if (gameId == Integer.valueOf(Math.toIntExact(p.game_id))) {
                count++;
            }
        }
        return count;
    }

    public List<PlayerModel> giveplayeringame(Integer gameId) {

        List<PlayerModel> toRet = new ArrayList<>();
        for (PlayerModel p : playerRepository.listAll()) {
            if (gameId == Integer.valueOf(Math.toIntExact(p.game_id))) {
                toRet.add(p);
            }
        }
        return toRet;
    }

    public GameDetailResponse joinGame(String id, String name) {
        System.out.println("id  is = " + id);
        System.out.println("name is = " + name);


        int gameid = Integer.parseInt(id);
        for (GameEntity e : GameConverter.convertModelToEntityList(gameRepository.listAll())) {

            if (e.id == gameid) {

                System.out.println("found the game");
                System.out.println("Player number is " + getPlayersCount(Math.toIntExact(e.id)));
                if (e.state == State.FINISHED || e.state == State.RUNNING || getPlayersCount(Math.toIntExact(e.id)) >= 4) {
                    return getGameInfo(String.valueOf(gameid));
                }
                System.out.println("passed");
                int pcount = getPlayersCount(gameid);
                int x = 1;
                int y = 1;
                if (pcount == 1) {
                    x = 15;
                    y = 1;
                } else if (pcount == 2) {
                    x = 15;
                    y = 13;
                } else {
                    x = 1;
                    y = 13;
                }
                createPlayerAndAdd(x, y, name, gameid);
                return getGameInfo(String.valueOf(gameid));
            }
        }

        return null;
    }


    public GameDetailResponse StartGame(String gameId) {


        int game_id = Integer.parseInt(gameId);
        for (GameEntity e : GameConverter.convertModelToEntityList(gameRepository.listAll())) {
            if (e.id == game_id) {
                GameModel inst;
                for (GameModel mode : gameRepository.listAll()) {
                    inst = mode;
                    if (mode.id == e.id) {
                        if (e.state == State.STARTING && getPlayersCount(game_id) == 1) {
                            e.state = State.FINISHED;
                            inst.state = "FINISHED";
                            gameRepository.persist(inst);

                        } else if (e.state == State.STARTING) {
                            e.state = State.RUNNING;
                            inst.state = "RUNNING";
                            gameRepository.persist(inst);
                        } else if (e.state == State.FINISHED) {
                            return null;
                        }
                    }
                }

                return getGameInfo(gameId);
            }
        }
        return null;
    }

    public List<List<Character>> getmaparr(List<String> map)
    {
        List<List<Character>> intmap = new ArrayList<>();
        for (String line : map) {
            List<Character> toAdd = new ArrayList<>();
            Integer i = 0;
            while (i < line.length()) {
                Character cur = line.charAt(i);
                i++;
                for (int b = 0; b < cur - 48; b++) {
                    toAdd.add(line.charAt(i));
                }
                i++;
            }
            intmap.add(toAdd);
        }
        return intmap;
    }

    public List<String> getmaptonormal(List<List<Character>> map )
    {
        List<String> toRet = new ArrayList<>();
        for (List<Character> line : map) {
            String toAdd = "";
            for (Integer i = 0; i < line.size(); i++)
            {
                Character act = line.get(i);
                int nb = 1;
                while (i+1< line.size() && line.get(i+1) == act && nb <10)
                {
                    if (nb == 9 )
                    {
                        break;
                    }
                    i++;
                    nb++;
                }
                toAdd = toAdd+nb;
                toAdd= toAdd+act;
            }
            toRet.add(toAdd);
        }
        return toRet;
    }

    public Boolean CheckValidPos(int x, int y, Long gameid) {

        List<String> map = null;
        for (GameModel gm : gameRepository.listAll()) {
            if (gameid == gm.id) {
                map = gm.map;
            }
        }
        if (map == null) {
            return false;
        }
        List<List<Character>> intmap = new ArrayList<>();
        for (String line : map) {
            List<Character> toAdd = new ArrayList<>();
            Integer i = 0;
            while (i < line.length()) {
                Character cur = line.charAt(i);
                i++;
                for (int b = 0; b < cur - 48; b++) {
                    toAdd.add(line.charAt(i));
                }
                i++;
            }
            intmap.add(toAdd);

        }
        if (intmap.get(y).get(x) != 'G') {
            System.out.println("printing map");
            for (List<Character> top : intmap)
            {
                System.out.println(top);
            }
            System.out.println("done");
            System.out.println("pos is : "+intmap.get(y).get(x));
            return false;
        }
        return true;
    }


    public GameDetailResponse MovePlayer(String gameId, String playerId, int PosX, int PosY) {
        Long pid = Long.valueOf(Integer.valueOf(playerId));
        Boolean foundgame =false;
        for (GameEntity e : GameConverter.convertModelToEntityList(gameRepository.listAll())) {
            if (e.id == Long.valueOf(gameId))
            {
                System.out.println("id iterator  : "+gameId);
                System.out.println("id to find is : "+e.id);
                foundgame =true;
                if ( e.state != State.RUNNING) {
                    System.out.println("game not running");
                    return null;
                }
            }
        }
        System.out.println("a game was found");

        if (foundgame == false)
        {
            System.out.println("not found game");
            GameDetailResponse toret = new GameDetailResponse(null, null, null, null, null);
            toret.map = new ArrayList<>();
            toret.map.add("nofound");
            return toret;
        }

        if (giveplayeringame(Integer.valueOf(gameId)).size() == 0)
        {
            System.out.println("no  player");
            return null;
        }
        System.out.println("passed no player in a game");
        for (PlayerModel e : giveplayeringame(Integer.valueOf(gameId))) {
            if (e.id == pid) {
                if (CheckValidPos(PosX, PosY, e.game_id))
                {
                    if (!((Math.abs(e.posX-PosX)== 1 && Math.abs(e.posY-PosY) ==0) || (Math.abs(e.posX-PosX) == 0 && Math.abs(e.posY-PosY) ==1 )))
                    {
                        System.out.println("x = "+e.posX);
                        System.out.println("y = "+e.posX);
                        System.out.println("not in range");
                        System.out.println("x diff : "+Math.abs(e.posX-PosX) +"|| y diff : "+ Math.abs(e.posY-PosY));
                        return null;
                    }
                    System.out.println("passed range");
                    if (e.lives ==0)
                    {
                        System.out.println("no lives");
                        return null;
                    }
                    System.out.println("passed lives");
                    if (e.lastmovement == null) {
                        System.out.println("no last movement");
                        e.lastmovement = Timestamp.valueOf(LocalDateTime.now());
                        e.posY = PosY;
                        e.posX = PosX;
                        playerRepository.persist(e);
                        return getGameInfo(gameId);
                    }
                    System.out.println("passed last mov null");
                    Long movedelayticks = 0L;
                    if (System.getenv("JWS_DELAY_MOVEMENT") == null ||System.getenv("JWS_DELAY_MOVEMENT").isEmpty() )
                    {
                        movedelayticks = 4L;
                    }
                    else
                    {
                        movedelayticks =  Long.valueOf(System.getenv("JWS_DELAY_MOVEMENT"));
                    }
                    Long secpertick = 0L;

                    if (System.getenv("JWS_TICK_DURATION") == null || System.getenv("JWS_TICK_DURATION").isEmpty()){
                        secpertick = 1L;
                    }
                    else
                    {
                        secpertick =   Long.valueOf(System.getenv("JWS_TICK_DURATION"));
                    }
                    System.out.println("passed these");

                    Timestamp timenow = Timestamp.valueOf(LocalDateTime.now());
                    Timestamp timelastmove = e.lastmovement;

                    Long diffnowlast =
                            timenow.getTime() -timelastmove.getTime();
                    Long delaytime = movedelayticks * secpertick;

                    if (diffnowlast<delaytime) {
                        GameDetailResponse toret = new GameDetailResponse(null, null, null, null, null);
                        toret.map =new ArrayList<>();
                        toret.map.add("notime");
                        System.out.println("no time range");
                        return toret;
                    }
                    System.out.println("passed delayed time ");

                    e.posY = PosY;
                    e.posX = PosX;
                    playerRepository.persist(e);
                    System.out.println("moved player");
                    return getGameInfo(gameId);
                }
                System.out.println("did not check the move ");
                return null;
            }
        }
        GameDetailResponse toret = new GameDetailResponse(null, null, null, null, null);
        toret.map = new ArrayList<>();
        toret.map.add("nofound");
        System.out.println("not found");
        return toret;
    }

    public GameDetailResponse placeBomb(String gameId,String playerId,int posX,int posY)
    {
        Long gameidint = Long.valueOf(gameId);
        Long playid = Long.valueOf(playerId);


        for (GameModel g :  gameRepository.listAll()) {
            System.out.println("seeing games");
            if (g.id == gameidint) {
                System.out.println("checking states");
                System.out.println("game state = "+g.state);
                if (!g.state.equals("RUNNING")) {
                    //return 400 not running
                    GameDetailResponse toret = new GameDetailResponse(null, null, null, null, null);
                    toret.map = new ArrayList<>();
                    toret.map.add("400");
                    System.out.println("err 400: game not running");
                    return toret;
                }
            }
            for (PlayerModel p : giveplayeringame(Math.toIntExact(gameidint)))
            {
                if (p.id == playid)
                {
                    System.out.println("lives = "+p.lives);
                    System.out.println("pos x = "+p.posX+" pos y = "+p.posY);
                    System.out.println("x = "+posX+" y = "+posY);
                    if (p.lives == 0 || p.posX != posX || p.posY != posY)
                    {
                        //return 400 / player
                        GameDetailResponse toret = new GameDetailResponse(null, null, null, null, null);
                        toret.map = new ArrayList<>();
                        toret.map.add("400");
                        System.out.println("err 400: lives <0 or posx posy loin");
                        return toret;
                    }

                    if (p.lastbomb != null)
                    {
                        //check time
                        if (!checktime(p))
                        {
                            System.out.println("err 429: too many bomb small time");
                            return null;
                        }
                    }
                    //place bomb
                    placeitact(p,g);
                    System.out.println("-------printing bomb---------");
                    for (String l : g.map)
                    {
                        System.out.println(l);
                    }
                    System.out.println("-------printing bomb---------");

                    return  getGameInfo(gameId);
                }
            }
            //return 404 player not found
            GameDetailResponse toret = new GameDetailResponse(null, null, null, null, null);
            toret.map = new ArrayList<>();
            toret.map.add("404");
            System.out.println("404");
            return toret;
        }
        //return 404 game not found
        GameDetailResponse toret = new GameDetailResponse(null, null, null, null, null);
        toret.map = new ArrayList<>();
        toret.map.add("404");
        System.out.println("404");
        return toret;
    }
    public List<String> arrtomap(List<List<Character>> map )
    {
        List<String> toRet = new ArrayList<>();
        for (List<Character> line : map) {
            String toAdd = "";
            for (Integer i = 0; i < line.size(); i++)
            {
                Character act = line.get(i);
                int nb = 1;
                while (i+1< line.size() && line.get(i+1) == act && nb <10)
                {
                    if (nb == 9 )
                    {
                        break;
                    }
                    i++;
                    nb++;
                }
                toAdd = toAdd+nb;
                toAdd= toAdd+act;
            }
            toRet.add(toAdd);
        }
        return toRet;
    }

    public void placeitact(PlayerModel p ,GameModel g) {
        System.out.println("enterring the placing bomb phase");
        List<List<Character>> easyMap = getmaparr(g.map);
        easyMap.get(p.posY).set(p.posX, 'B');
        g.map = getmaptonormal(easyMap);
        System.out.println("------------printing insiide bomb phasebefore persist-----------------------");
        for (String line : g.map) {
            System.out.println(line);
        }
        System.out.println("------------done pronting insiide bomb phasebefore persist-----------------------");
        gameRepository.persist(g);
        p.lastbomb = Timestamp.valueOf(LocalDateTime.now());
        playerRepository.persist(p);
        CompletableFuture todo = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("kaboom");
                    return null;
                }).thenAccept(result->functhread(p.posX,p.posY,g,p));
    }

    public void functhread(int x,int y, GameModel gm,PlayerModel pm) {
        System.out.println("enetering thread func");
        List<List<Character>> mymap = maptoarr(gm.map);
        mymap.get(y).set(x,'G');
        if (mymap.get(y).get(x-1) == 'W')
        {
            mymap.get(y).set(x-1,'G');
        }
        if (mymap.get(y).get(x+1) == 'W')
        {
            mymap.get(y).set(x+1,'G');
        }
        if (mymap.get(y-1).get(x) == 'W')
        {
            mymap.get(y-1).set(x,'G');
        }
        if (mymap.get(y+1).get(x) == 'W')
        {
            mymap.get(y+1).set(x,'G');
        }
        System.out.println("before persist");
        gm.map = arrtomap(mymap);
        System.out.println("done arr to map");
        gameRepository.persist(gm);
        System.out.println("after persist");
        List<PlayerModel> playerModelList = giveplayeringame(Math.toIntExact(gm.id));

        for (PlayerModel p :playerModelList)
        {
            if (((Math.abs(p.posX-x)== 1 && Math.abs(p.posY-y) ==0) || (Math.abs(p.posX-x) == 0 && Math.abs(p.posY-y) ==1 )))
            {
                p.lives -=1;
                playerRepository.persist(p);
            }
            int count = 0;
            for (PlayerModel px : playerModelList)
            {
                if (px.lives == 0)
                {
                    count +=1 ;
                }
            }
            if (count == playerModelList.size())
            {
                gm.state = "FINISHED";
                gameRepository.persist(gm);
                return;
            }
        }
        System.out.println("-------------bomb printed----------------");
        for (String line : gm.map)
        {
            System.out.println(line);
        }
        System.out.println("---------------bomb printed------------------");

    }
    public List<List<Character>> maptoarr(List<String> map)
    {
        List<List<Character>> intmap = new ArrayList<>();
        for (String line : map) {
            List<Character> toAdd = new ArrayList<>();
            Integer i = 0;
            while (i < line.length()) {
                Character cur = line.charAt(i);
                i++;
                for (int b = 0; b < cur - 48; b++) {
                    toAdd.add(line.charAt(i));
                }
                i++;
            }
            intmap.add(toAdd);
        }
        return intmap;
    }

    public boolean checktime(PlayerModel p)
    {
        Long movedelayticks = 0L;
        if (System.getenv("WS_DELAY_BOMB") == null ||System.getenv("WS_DELAY_BOMB").isEmpty() )
        {
            movedelayticks = 4L;
        }
        else
        {
            movedelayticks =  Long.valueOf(System.getenv("WS_DELAY_BOMB"));
        }
        Long secpertick = 0L;
        if (System.getenv("JWS_TICK_DURATION") == null || System.getenv("JWS_TICK_DURATION").isEmpty()){
            secpertick = 1L;
        }
        else
        {
            secpertick =   Long.valueOf(System.getenv("JWS_TICK_DURATION"));
        }
        Timestamp timenow = Timestamp.valueOf(LocalDateTime.now());
        Timestamp timelastmove = p.lastbomb;
        Long diffnowlast =
                timenow.getTime() -timelastmove.getTime();
        Long delaytime = movedelayticks * secpertick;
        System.out.println("Delay should be minimum: "+delaytime);
        System.out.println("       waited time was : "+diffnowlast);
        return diffnowlast>=delaytime*1000;
    }
}
