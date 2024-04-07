package fr.epita.assistants.jws.presentation.rest.response;

import fr.epita.assistants.jws.utils.State;

public class GetAllGameResponse {
  public Long id;
  public int players;
  public State state;



    public GetAllGameResponse(Long id,int count, State state) {
        this.id = id;
        this.players = count;
        this.state = state;
    }
}
