package com.project.openrun.global.kafka.consumer.exceptionHandler;

import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.product.repository.OpenRunProductRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "productErrorHandler")
@RequiredArgsConstructor
public class ProductErrorHandler implements CustomErrorHandler {

    private final ProductRepository productRepository;
    private final OpenRunProductRedisRepository openRunProductRedisRepository;

    @Override
    public void handle(ConsumerRecord<?, ?> consumerRecord, Exception exception) {
        OrderEventDto orderEventDto = (OrderEventDto) consumerRecord.value();
        log.info("Listner에서 주문 저장에 실패했습니다. 3번 실패");
        //redis에서만 올려준 것
        openRunProductRedisRepository.increaseQuantity(orderEventDto.getProductId(), orderEventDto.getOrderRequestDto().count());

        //DB에서만 올려준 것
        productRepository.updateProductQuantity(orderEventDto.getOrderRequestDto().count(), orderEventDto.getProductId());

    }
}