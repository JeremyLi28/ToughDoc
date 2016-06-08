package modules;

import controllers.Application.*;

import java.util.ArrayList;

/**
 * Delete.
 */
public class Delete extends Operation{
    private int position;
    public Delete(int userId, ArrayList<Integer> stateVector, int priority, int docId, int position) {
        super(userId, stateVector, Type.Delete, priority, docId);
        this.position = position;
    }

    public Delete(Delete delete) {
        super(delete.getUserID(), delete.getStateVector(), delete.getType(), delete.getPriority(), delete.getDocID());
        this.position = delete.position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        position = pos;
    }
}
