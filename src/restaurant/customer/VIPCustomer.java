package restaurant.customer;

public class VIPCustomer extends Customer {
    private static final int LOSS_PER_TURN = 5;
    private static final int DISCOUNT_PERCENT = 10;

    public VIPCustomer(int id) {
        super(id);
    }

    @Override
    public String getTypeName() {
        return "VIP";
    }

    @Override
    public int getDiscountPercent() {
        return DISCOUNT_PERCENT;
    }

    @Override
    protected int satisfactionLossPerTurn() {
        return LOSS_PER_TURN;
    }
}
