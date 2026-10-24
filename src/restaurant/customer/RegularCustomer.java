package restaurant.customer;

public class RegularCustomer extends Customer {
    private static final int LOSS_PER_TURN = 8;

    public RegularCustomer(int id) {
        super(id);
    }

    @Override
    public String getTypeName() {
        return "Regular";
    }

    @Override
    protected int satisfactionLossPerTurn() {
        return LOSS_PER_TURN;
    }
}
