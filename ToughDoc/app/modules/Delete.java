package modules;

import controllers.Application.*;
/**
 * Delete.
 */
public class Delete extends Operation{
    private int position;
    public Delete(int userId, int docId, int position) {
        super(userId, docId, Type.Delete);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
