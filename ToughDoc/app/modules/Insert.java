package modules;

import controllers.Application.*;
/**
 * Insert.
 */
public class Insert extends Operation{
    private  String character;
    private int position;
    public Insert (int userId, int docId, String character, int position) {
        super(userId, docId, Type.Insert);
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
