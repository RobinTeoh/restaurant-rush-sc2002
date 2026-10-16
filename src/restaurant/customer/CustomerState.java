package restaurant.customer;

public enum CustomerState {
    WAITING, SEATED, ORDERED, EATING, READY_TO_PAY, PAID, LEFT;

    public boolean losesSatisfaction() {
        return this == WAITING || this == SEATED || this == ORDERED;
    } //only lose satisfaction when state are these
}
