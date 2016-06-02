package modules;

import controllers.Application.*;
/**
 * Delete.
 */
public class Delete extends Operation{
    private int position;
    public Delete(int position, int docId) {
        super(docId, Type.Delete);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
