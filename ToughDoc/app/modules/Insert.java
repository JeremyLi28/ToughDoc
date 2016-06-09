package modules;

import controllers.Application.*;

import java.util.ArrayList;

/**
 * Insert.
 */
public class Insert extends Operation{
    private  String character;
    private int position;
    public Insert (int userId, ArrayList<Integer> stateVector, int priority, int docId, String character, int position) {
        super(userId, stateVector, Type.Insert, priority, docId);
        this.character = character;
        this.position = position;
    }

    public Insert(Insert insert) {
        super(insert.getUserId(), insert.getStateVector(), insert.getType(), insert.getPriority(), insert.getDocID());
        this.character = insert.character;
        this.position = insert.position;
    }

    public String getCharacter(){
        return character;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        position = pos;
    }
}
