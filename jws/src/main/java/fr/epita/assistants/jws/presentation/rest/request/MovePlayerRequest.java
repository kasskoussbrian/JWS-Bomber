package fr.epita.assistants.jws.presentation.rest.request;

public class MovePlayerRequest {
    public int posX;
    public int posY;

    public MovePlayerRequest(int posX,int posY) {
        this.posY =posY;
        this.posX=posX;
    }

}
