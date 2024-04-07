package fr.epita.assistants.jws.utils;

import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.data.repository.GameRepository;
import fr.epita.assistants.jws.data.repository.PlayerRepository;
import fr.epita.assistants.jws.domain.service.GameService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Bombtimer extends Thread{

    @Inject
    GameService gameService;

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
    public List<PlayerModel> giveplayeringame(Integer gameId) {

        List<PlayerModel> toRet = new ArrayList<>();
        for (PlayerModel p : gameService.playerRepository.listAll()) {
            if (gameId == Integer.valueOf(Math.toIntExact(p.game_id))) {
                toRet.add(p);
            }
        }
        return toRet;
    }
   public int x;
   public int y;

   GameModel gm;
   PlayerModel pm;

    public Bombtimer(int x, int y, GameModel gameModel, PlayerModel playerModel) {
        this.x = x;
        this.y = y;
        gm=gameModel;
        pm=playerModel;
    }

    public void run()
    {

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("booooom");

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
       gameService.gameRepository.persist(gm);
        System.out.println("after persist");
        List<PlayerModel> playerModelList = giveplayeringame(Math.toIntExact(gm.id));

        for (PlayerModel p :playerModelList)
        {
            if (((Math.abs(p.posX-x)== 1 && Math.abs(p.posY-y) ==0) || (Math.abs(p.posX-x) == 0 && Math.abs(p.posY-y) ==1 )))
            {
                p.lives -=1;
              gameService.playerRepository.persist(p);
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
               gameService.gameRepository.persist(gm);
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
}
