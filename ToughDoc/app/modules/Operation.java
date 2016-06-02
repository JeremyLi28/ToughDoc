package modules;

import java.io.Serializable;
import controllers.Application.*;

/**
 * Operation.
 */


public abstract class Operation implements Serializable{

    private int docID;
    private int userID;
    private Type type;
    public Operation(int userID, int docID, Type type) {
        this.userID = userID;
        this.docID = docID;
        this.type = type;
    }

    public int getUserID() { return userID; }

    public int getDocID() {
        return docID;
    }

    public Type getType() { return type; }
}

