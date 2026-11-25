package org.bootcamp.ecommerce.utils.validators;

import org.bootcamp.ecommerce.enums.OrderStatusEnum;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class OrderStatusValidator {


    private static final Map<OrderStatusEnum, Set<OrderStatusEnum>> allowedTransitions = new EnumMap<>(OrderStatusEnum.class);

    static {
        allowedTransitions.put(OrderStatusEnum.ORDER_PLACED, Set.of(
                OrderStatusEnum.CANCELLED,
                OrderStatusEnum.ORDER_CONFIRMED,
                OrderStatusEnum.ORDER_REJECTED
        ));

        allowedTransitions.put(OrderStatusEnum.CANCELLED, Set.of(
                OrderStatusEnum.REFUND_INITIATED
        ));

        allowedTransitions.put(OrderStatusEnum.ORDER_REJECTED, Set.of(
                OrderStatusEnum.REFUND_INITIATED,
                OrderStatusEnum.CLOSED
        ));

        allowedTransitions.put(OrderStatusEnum.ORDER_CONFIRMED, Set.of(
                OrderStatusEnum.CANCELLED,
                OrderStatusEnum.ORDER_SHIPPED
        ));

        allowedTransitions.put(OrderStatusEnum.ORDER_SHIPPED, Set.of(
                OrderStatusEnum.DELIVERED
        ));

        allowedTransitions.put(OrderStatusEnum.DELIVERED, Set.of(
                OrderStatusEnum.RETURN_REQUESTED
        ));

        allowedTransitions.put(OrderStatusEnum.RETURN_REQUESTED, Set.of(
                OrderStatusEnum.CLOSED,
                OrderStatusEnum.RETURN_REJECTED,
                OrderStatusEnum.RETURN_APPROVED
        ));

        allowedTransitions.put(OrderStatusEnum.RETURN_REJECTED, Set.of(
                OrderStatusEnum.CLOSED
        ));

        allowedTransitions.put(OrderStatusEnum.RETURN_APPROVED, Set.of(
                OrderStatusEnum.PICK_UP_INITIATED
        ));

        allowedTransitions.put(OrderStatusEnum.PICK_UP_INITIATED, Set.of(
                OrderStatusEnum.PICK_UP_COMPLETED
        ));

        allowedTransitions.put(OrderStatusEnum.PICK_UP_COMPLETED, Set.of(
                OrderStatusEnum.REFUND_INITIATED
        ));

        allowedTransitions.put(OrderStatusEnum.REFUND_INITIATED, Set.of(
                OrderStatusEnum.REFUND_COMPLETED
        ));

        allowedTransitions.put(OrderStatusEnum.REFUND_COMPLETED, Set.of(
                OrderStatusEnum.CLOSED
        ));
    }

    public static boolean isValidTransition(OrderStatusEnum from, OrderStatusEnum to) {
        return allowedTransitions.getOrDefault(from, Collections.emptySet()).contains(to);
    }

    private OrderStatusValidator() {
    }
}
