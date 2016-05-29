package modules;

import java.io.Serializable;

/**
 * Operation.
 */

enum Type {
    Insert, Delete
}
public abstract class Operation implements Serializable{

    private int docID;
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

