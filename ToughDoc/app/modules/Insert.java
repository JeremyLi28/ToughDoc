package modules;

import controllers.Application.*;

import java.util.ArrayList;

/**
 * Insert.
 */
public class Insert extends Operation{
    private  String character;
    private int position;
    public Insert (int userId, ArrayList<Integer> stateVector, int priority, int docId, String character, int position,) {
        super(userId, stateVector, Type.Insert, priority, docId);
        this.character = character;
        this.position = position;
    }

    public String getCharacter(){
        return character;
    }

    public int getPosition() {
        return position;
    }
}
