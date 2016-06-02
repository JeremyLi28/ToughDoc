package modules;

import controllers.Application.*;
/**
 * Insert.
 */
public class Insert extends Operation{
    private  String character;
    private int position;
    public Insert (String character, int position, int docId) {
        super(docId, Type.Insert);
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
