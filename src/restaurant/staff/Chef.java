package restaurant.staff;

import restaurant.order.Order;

/**
 * The single Chef (Stage 1). Does one prep unit of work per turn, on the order the
 * manager picks. The order itself keeps track of its progress.
 */
public class Chef {

    /**
     * Works on the order for one prep unit (CONTRACT.md C -> B).
     * Returns false, with no change, if the order is READY, SERVED, PAID or CANCELLED.
     */
    public boolean work(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }
        return order.prepareOneUnit();
    }
}
