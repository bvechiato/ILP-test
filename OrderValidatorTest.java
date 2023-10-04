package uk.ac.ed.info;

import junit.framework.TestCase;
import org.junit.jupiter.api.RepeatedTest;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;


public class OrderValidatorTest extends TestCase
{
    public static Order createValidOrder() {
        var order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));

        order.setCreditCardInformation(
                new CreditCardInformation(
                        "0123456789101234",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 30)),
                        "222"
                )
        );

        // every order has the defined outcome
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

        return order;
    }

    public static Restaurant createValidRestaurant() {
        return new Restaurant("testRestaurant",
                new LngLat(55.945535152517735, -3.1912869215011597),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                new Pizza[]{new Pizza("Pizza A", 2300)}
        );
    }

    public static Order createValidPizza(Restaurant restaurant, Order order) {
        // get a random restaurant
        // and load the order items plus the price
        Pizza pizza = restaurant.menu()[0];
        order.setPizzasInOrder(new Pizza[]{pizza});
        order.setPriceTotalInPence(pizza.priceInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE);

        return order;
    }

    public void displayOrder(Order order) {
        System.err.println("\n -- Order info \nOrderValidationCode: " +
                order.getOrderValidationCode() +
                "\nOrderStatus: " +
                order.getOrderStatus() +
                "\nOrderNo: " +
                order.getOrderNo() +
                "\nOrder Contents: " +
                Arrays.toString(order.getPizzasInOrder()) +
                "\n -- Credit card info \nCreditCardNumber: " +
                order.getCreditCardInformation().getCreditCardNumber() +
                "\nCCV: " +
                order.getCreditCardInformation().getCvv() +
                "\nExpiryDate: " +
                order.getCreditCardInformation().getCreditCardExpiry()
        );
    }

    @RepeatedTest(100)
    public void testCreditCardNumber16Numbers() {
        Order order = createValidOrder();

        int leftLimit = 48;
        int rightLimit = 57;
        Random random = new Random();
        int targetStringLength = 16;


        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCreditCardNumber(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardNumberOnlyNumbers() {
        Order order = createValidOrder();

        int leftLimit = 48;
        int rightLimit = 57;
        Random random = new Random();
        int targetStringLength = 16;

        while (targetStringLength == 16) {
            targetStringLength = (int)(Math.random()*100);
        }

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCreditCardNumber(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardNumber16Characters() {
        Order order = createValidOrder();

        int leftLimit = 0;
        int rightLimit = 127;
        Random random = new Random();
        int targetStringLength = 16;


        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCreditCardNumber(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder.getOrderValidationCode());
    }

}
