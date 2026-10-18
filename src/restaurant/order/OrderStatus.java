package restaurant.order;

public enum OrderStatus {
    PLACED, READY, SERVED, PAID, CANCELLED;

    /** Only an order the customer can still walk away from may be cancelled (DECISIONS D3). */
    public boolean canBeCancelled() {
        return this == PLACED || this == READY;
    }
}
