package modules;

import java.io.Serializable;
import java.util.ArrayList;

import controllers.Application.*;

/**
 * Operation.
 */


public abstract class Operation implements Serializable{

    private int docID;
    private int userId;
    private Type type;
    private ArrayList<Integer> stateVector;
    private int priority;
    public Operation(int userID,ArrayList<Integer> stateVector, Type type , int priority, int docID) {
        this.userId = userID;
        this.type = type;
        this.stateVector = stateVector;
        this.priority = priority;
        this.docID = docID;
    }

    public int getUserId() { return userId; }

    public int getDocID() {
        return docID;
    }

    public Type getType() { return type; }

    public ArrayList<Integer> getStateVector() {
        return stateVector;
    }

    public int getPriority() { return priority; }
}

