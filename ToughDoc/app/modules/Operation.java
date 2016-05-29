package modules;

import java.io.Serializable;
import controllers.Application.*;

/**
 * Operation.
 */


public abstract class Operation implements Serializable{

    private int docID = 0;
    private Type type;
    public Operation() {}
    public Operation(int docID, Type type) {
        this.docID = docID;
        this.type = type;
    }

    public int getDocID() {
        return docID;
    }
}

