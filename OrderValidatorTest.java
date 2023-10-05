package uk.ac.ed.info;

import junit.framework.TestCase;
import org.junit.jupiter.api.RepeatedTest;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                        "0000000000000000",
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
        // Creates valid restaurant
        return new Restaurant("testRestaurant",
                new LngLat(55.945535152517735, -3.1912869215011597),
                new DayOfWeek[] {
                        DayOfWeek.MONDAY, DayOfWeek.FRIDAY
                },
                new Pizza[]{
                        new Pizza("Pizza A", 2300),
                        new Pizza("Pizza B", 2400),
                        new Pizza("Pizza C", 2500)
                }
        ) ;
    }

    public static Order createValidPizza(Restaurant restaurant, Order order) {
        ArrayList<Pizza> currentOrder = new ArrayList<>();
        Pizza[] pizzas = order.getPizzasInOrder();
        if (pizzas.length > 0) {
            Collections.addAll(currentOrder, pizzas);
        }

        // Takes first pizza from given restaurant
        Pizza pizza = restaurant.menu()[0];
        int currentPrice = order.getPriceTotalInPence();

        currentOrder.add(pizza);

        Pizza[] newOrder = currentOrder.toArray(new Pizza[0]);

        order.setPizzasInOrder(newOrder);

        // If there was nothing in the order
        if (currentPrice == 0) {
            order.setPriceTotalInPence(pizza.priceInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE);
        } else {
            order.setPriceTotalInPence(currentPrice + pizza.priceInPence());
        }

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
                "\nPriceTotalInPence: " +
                order.getPriceTotalInPence() +
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

    @RepeatedTest(100)
    public void testCreditCardCVVDigits() {
        Order order = createValidOrder();

        int leftLimit = 0;
        int rightLimit = 127;
        Random random = new Random();
        int targetStringLength = 3;

        while (targetStringLength == 3) {
            targetStringLength = (int)(Math.random()*100);
        }

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCvv(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCVVNumbers() {
        Order order = createValidOrder();

        int leftLimit = 48;
        int rightLimit = 57;
        Random random = new Random();
        int targetStringLength = 3;

        while (targetStringLength == 3) {
            targetStringLength = (int)(Math.random()*100);
        }

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCvv(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCVV3Digits() {
        Order order = createValidOrder();

        int leftLimit = 0;
        int rightLimit = 127;
        Random random = new Random();
        int targetStringLength = 3;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCvv(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCVV3Numbers() {
        Order order = createValidOrder();

        int leftLimit = 48;
        int rightLimit = 57;
        Random random = new Random();
        int targetStringLength = 3;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCvv(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDateDigits() {
        Order order = createValidOrder();

        int leftLimit = 0;
        int rightLimit = 127;
        Random random = new Random();
        int targetStringLength = 5;

        while (targetStringLength == 5) {
            targetStringLength = (int)(Math.random()*100);
        }

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDate5Digits() {
        Order order = createValidOrder();

        int leftLimit = 0;
        int rightLimit = 127;
        Random random = new Random();
        int targetStringLength = 5;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDate5Numbers() {
        Order order = createValidOrder();

        int leftLimit = 48;
        int rightLimit = 57;
        Random random = new Random();
        int targetStringLength = 5;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDate4NumbersRandomSeparator() {
        Order order = createValidOrder();

        int leftLimit = 48;
        int rightLimit = 57;
        Random random = new Random();
        int targetStringLength = 5;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        char c = (char) ('a' + random.nextInt(26));

        generatedString = generatedString.substring(0,2) + c + generatedString.substring(2);

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDateInPast() {
        Order order = createValidOrder();

        int leftLimit = 48;
        int rightLimit = 57;
        Random random = new Random();
        int targetStringLength = 2;

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        int currentYear = LocalDate.now().getYear() - 2000;

        int randomYear = ThreadLocalRandom.current().nextInt(10, currentYear - 1);

        generatedString = generatedString + '/' + randomYear;

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDateValidMonthInFuture() {
        Order order = createValidOrder();

        int currentYear = LocalDate.now().getYear() - 2000;

        int randomMonth = ThreadLocalRandom.current().nextInt(1, 13);
        int randomYear = ThreadLocalRandom.current().nextInt(currentYear + 1, 99);


        String generatedString;
        if (randomMonth < 10) {
            generatedString = "0" + randomMonth + '/' + randomYear;
        } else {
            generatedString = Integer.toString(randomMonth) + '/' + randomYear;
        }

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDateInvalidMonthInFuture() {
        Order order = createValidOrder();

        int currentYear = LocalDate.now().getYear() - 2000;

        int randomMonth = ThreadLocalRandom.current().nextInt(13, 100);
        int randomYear = ThreadLocalRandom.current().nextInt(currentYear + 1, 99);

        String generatedString = Integer.toString(randomMonth) + '/' + randomYear;

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDateInvalidMonthCurrentYear() {
        Order order = createValidOrder();

        int currentYear = LocalDate.now().getYear() - 2000;

        int randomMonth = ThreadLocalRandom.current().nextInt(13, 100);

        String generatedString = Integer.toString(randomMonth) + '/' + currentYear;

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDateInvalidDate() {
        Order order = createValidOrder();

        int currentYear = LocalDate.now().getYear() - 2000;

        int randomMonth = ThreadLocalRandom.current().nextInt(13, 99);
        int randomYear = ThreadLocalRandom.current().nextInt(0,currentYear);

        String generatedString;
        if (randomYear < 10) {
            generatedString = Integer.toString(randomMonth) + "/0" + randomYear;
        } else {
            generatedString = Integer.toString(randomMonth) + '/' + randomYear;
        }

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testCreditCardCExpiryDateCurrentDate() {
        Order order = createValidOrder();

        int currentYear = LocalDate.now().getYear() - 2000;
        int currentMonth = LocalDate.now().getMonthValue();

        String generatedString = Integer.toString(currentMonth) + '/' + currentYear;

        order.getCreditCardInformation().setCreditCardExpiry(generatedString);

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testTotalPriceIsNegative() {
        Order order = createValidOrder();

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        int randomNumber = ThreadLocalRandom.current().nextInt(-100000, 0);

        order.setPriceTotalInPence(randomNumber);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
    }

    public void testTotalPriceIsZero() {
        Order order = createValidOrder();

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        order.setPriceTotalInPence(0);

        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus()) ;
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testTotalPriceTwoPizzas() {
        Order order = createValidOrder();

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);
        order = createValidPizza(restaurant, order);

        int randomNumber = ThreadLocalRandom.current().nextInt(-100000, 0);

        order.setPriceTotalInPence(randomNumber);
        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
    }


    /*
     * testTotalPriceNoPizzas isn't necessary,
     * we assume there is at least one pizza RE Glienecke @66_f1 on Piazza
     */

    @RepeatedTest(100)
    public void testTotalPriceOnePizzaNoOrderFee() {
        Order order = createValidOrder();

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);

        int oldPrice = order.getPriceTotalInPence();
        order.setPriceTotalInPence(oldPrice - SystemConstants.ORDER_CHARGE_IN_PENCE);

        int randomNumber = ThreadLocalRandom.current().nextInt(-100000, 0);

        order.setPriceTotalInPence(randomNumber);
        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
    }

    @RepeatedTest(100)
    public void testTotalPriceTwoPizzasOrderFeeTwice() {
        Order order = createValidOrder();

        Restaurant restaurant = createValidRestaurant();

        order = createValidPizza(restaurant, order);
        order = createValidPizza(restaurant, order);

        int oldPrice = order.getPriceTotalInPence();
        order.setPriceTotalInPence(oldPrice + SystemConstants.ORDER_CHARGE_IN_PENCE);

        int randomNumber = ThreadLocalRandom.current().nextInt(-100000, 0);

        order.setPriceTotalInPence(randomNumber);
        Order validatedOrder = new OrderValidator().validateOrder(order, new Restaurant[]{restaurant});

        displayOrder(validatedOrder);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode());
    }
}
