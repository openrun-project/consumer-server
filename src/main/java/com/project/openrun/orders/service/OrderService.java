package com.project.openrun.orders.service;

import com.project.openrun.global.kafka.exception.type.ErrorCode;
import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.entity.OrderStatus;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.OpenRunProductRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.project.openrun.global.kafka.exception.type.ErrorCode.NOT_FOUND_DATA;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OpenRunProductRedisRepository openRunProductRedisRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void saveOrder(Long productId, Member member, Integer count){

        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("존재하지 않는 상품"))
        );

        product.updateCurrentQuantity(openRunProductRedisRepository.getCurrentQuantityCount(productId));

        Order order = Order.builder()
                .member(member)
                .product(product)
                .count(count)
                .totalPrice(product.getPrice() * count)
                .orderStatus(OrderStatus.SUCCESS)
                .build();

        orderRepository.save(order);
    }

    @Transactional
    public void saveFailOrder(Long productId, Member member, Integer count){
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND_DATA.getStatus(), NOT_FOUND_DATA.formatMessage("존재하지 않는 상품"))
        );
        
        Order order = Order.builder()
                .member(member)
                .product(product)
                .count(count)
                .totalPrice(product.getPrice() * count)
                .orderStatus(OrderStatus.FAIL)
                .build();

        orderRepository.save(order);
    }

}
