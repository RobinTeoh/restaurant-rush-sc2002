package restaurant.staff;

import java.util.List;

import restaurant.control.ActionResult;
import restaurant.customer.Customer;
import restaurant.place.Table;
import restaurant.place.WaitingQueue;

public class Host {
    public ActionResult seatFirstInQueue(WaitingQueue queue, List<Table> tables) {
        if (queue.isEmpty()) {
            return ActionResult.failure("The Host has nobody to seat.");
        }
        Table freeTable = null;
        for (Table t : tables) {
            if (t.isFree()) {
                freeTable = t;
                break;
            }
        }
        if (freeTable == null) {
            return ActionResult.failure("The Host has no free table.");
        }
        Customer first = queue.list().get(0);
        queue.remove(first);
        freeTable.assign(first);
        first.seat();
        return ActionResult.success("Host seated customer " + first.getId()
                                + " at table " + freeTable.getId() + ".");       
    }
}
