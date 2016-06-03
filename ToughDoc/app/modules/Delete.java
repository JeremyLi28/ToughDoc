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

    public int getPosition() {
        return position;
    }
}
