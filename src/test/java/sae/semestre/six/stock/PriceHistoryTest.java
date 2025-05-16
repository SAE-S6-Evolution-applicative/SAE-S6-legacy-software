package sae.semestre.six.stock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PriceHistoryTest {

    @Test
    void testGetPriceIncrease() {
        PriceHistory priceHistory = new PriceHistory();
        double OLD_PRICE = 10.0;
        double NEW_PRICE = 20.0;
        priceHistory.setOldPrice(OLD_PRICE);
        priceHistory.setNewPrice(NEW_PRICE);
        assertEquals(NEW_PRICE - OLD_PRICE, priceHistory.getPriceIncrease());

        OLD_PRICE = 30.0;
        NEW_PRICE = 10.0;
        priceHistory.setOldPrice(OLD_PRICE);
        priceHistory.setNewPrice(NEW_PRICE);
        assertEquals(NEW_PRICE - OLD_PRICE, priceHistory.getPriceIncrease());
    }

    @Test
    void testGetPercentageChange() {
        PriceHistory priceHistory = new PriceHistory();
        double OLD_PRICE = 10.0;
        double NEW_PRICE = 20.0;
        priceHistory.setOldPrice(OLD_PRICE);
        priceHistory.setNewPrice(NEW_PRICE);
        assertEquals(priceHistory.getPriceIncrease() / OLD_PRICE * 100, priceHistory.getPercentageChange());
    }

    @Test
    void testGetPercentageChangeWithZero() {
        PriceHistory priceHistory = new PriceHistory();
        double OLD_PRICE = 0.0;
        double NEW_PRICE = 20.0;
        priceHistory.setOldPrice(OLD_PRICE);
        priceHistory.setNewPrice(NEW_PRICE);
        assertNotEquals(200, priceHistory.getPercentageChange());
        assertEquals(Double.POSITIVE_INFINITY, priceHistory.getPercentageChange(), "We divided by zero so the result should be Infinity");

        NEW_PRICE = -20.0;
        priceHistory.setNewPrice(NEW_PRICE);
        assertNotEquals(-200, priceHistory.getPercentageChange());
        assertEquals(Double.NEGATIVE_INFINITY, priceHistory.getPercentageChange(), "We divided by zero so the result should be Infinity");
    }
}