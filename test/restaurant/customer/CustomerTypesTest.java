package restaurant.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import restaurant.menu.Menu;

// what is different between the 3 types (brief 4.2)
class CustomerTypesTest {

    @Test
    void eachTypeLosesItsOwnAmountPerWaitingTurn() {
        Customer regular = new RegularCustomer(1);
        Customer vip = new VIPCustomer(2);
        Customer critic = new CriticCustomer(3);

        regular.loseSatisfaction();
        vip.loseSatisfaction();
        critic.loseSatisfaction();

        assertEquals(92, regular.getSatisfaction());
        assertEquals(95, vip.getSatisfaction());
        assertEquals(88, critic.getSatisfaction());
    }

    @Test
    void criticLeavesFirstWhenIgnored() {
        // 100 / 12 -> 9 turns to reach 0, regular needs 13, vip needs 20
        Customer critic = new CriticCustomer(1);
        for (int turn = 1; turn <= 8; turn++) {
            critic.loseSatisfaction();
        }
        assertEquals(4, critic.getSatisfaction());
        critic.loseSatisfaction();
        assertEquals(CustomerState.LEFT, critic.getState());
    }

    @Test
    void vipNeedsTwentyTurnsToLeave() {
        Customer vip = new VIPCustomer(1);
        for (int turn = 1; turn <= 19; turn++) {
            vip.loseSatisfaction();
        }
        assertEquals(5, vip.getSatisfaction());
        vip.loseSatisfaction();
        assertEquals(CustomerState.LEFT, vip.getState());
    }

    @Test
    void onlyVipGetsADiscount() {
        assertEquals(0, new RegularCustomer(1).getDiscountPercent());
        assertEquals(10, new VIPCustomer(1).getDiscountPercent());
        assertEquals(0, new CriticCustomer(1).getDiscountPercent());
    }

    @Test
    void typeNamesForTheDisplay() {
        assertEquals("Regular", new RegularCustomer(1).getTypeName());
        assertEquals("VIP", new VIPCustomer(1).getTypeName());
        assertEquals("Critic", new CriticCustomer(1).getTypeName());
    }

    @Test
    void criticAlwaysOrdersTheMostExpensiveDish() {
        Menu menu = Menu.standard();
        for (int id = 1; id <= 6; id++) {
            Customer critic = new CriticCustomer(id);
            critic.seat();
            assertEquals("Pasta", critic.chooseFood(menu).getName());
        }
    }

    @Test
    void vipChoosesFoodLikeARegularCustomer() {
        // same seed rule, so a VIP with the same id gets the same dish as a regular
        Menu menu = Menu.standard();
        for (int id = 1; id <= 6; id++) {
            Customer regular = new RegularCustomer(id);
            Customer vip = new VIPCustomer(id);
            regular.seat();
            vip.seat();
            assertEquals(regular.chooseFood(menu), vip.chooseFood(menu));
        }
    }

    @Test
    void criticStillFollowsTheOrderingRules() {
        Menu menu = Menu.standard();
        Customer critic = new CriticCustomer(1);
        assertThrows(IllegalStateException.class, () -> critic.chooseFood(menu)); // not seated yet
        critic.seat();
        critic.chooseFood(menu);
        assertThrows(IllegalStateException.class, () -> critic.chooseFood(menu)); // already ordered
    }
}
