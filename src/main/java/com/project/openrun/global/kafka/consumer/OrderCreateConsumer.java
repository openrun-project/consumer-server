package com.project.openrun.global.kafka.consumer;


import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreateConsumer {
    private final OrderService orderService;

    public void createOrderInConsumer(OrderEventDto orderEventDto) {
        orderService.saveOrder(orderEventDto.getProductId(), orderEventDto.getMember(), orderEventDto.getOrderRequestDto().count());
    }
}


