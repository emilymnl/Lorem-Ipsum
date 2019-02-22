package inf112.skeleton.app;

import inf112.skeleton.app.Interfaces.IGameObject;

abstract class GameObject implements IGameObject {

    private Position position;
    private Board board;
    private String objectID;

    public GameObject(Position position, String objectID, Board board){
        this.board = board;
        this.objectID = objectID;
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void place(Position position) {

    }

    @Override
    public void remove() {

    }
}
