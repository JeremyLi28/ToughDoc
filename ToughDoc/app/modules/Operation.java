package modules;

import java.io.Serializable;
import java.util.ArrayList;

import controllers.Application.*;

/**
 * Operation.
 */


public abstract class Operation implements Serializable{

    private int docID;
    private int userID;
    private Type type;
    private ArrayList<Integer> stateVector;
    private int priority;
    public Operation(int userID,ArrayList<Integer> stateVector, Type type , int priority, int docID) {
        this.userID = userID;
        this.type = type;
        this.stateVector = stateVector;
        this.priority = priority;
        this.docID = docID;
    }

    public int getUserID() { return userID; }

    public int getDocID() {
        return docID;
    }

    public Type getType() { return type; }
}

