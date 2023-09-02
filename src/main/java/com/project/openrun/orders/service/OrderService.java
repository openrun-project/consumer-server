package com.project.openrun.orders.service;

import com.project.openrun.member.entity.Member;
import com.project.openrun.orders.entity.Order;
import com.project.openrun.orders.repository.OrderRepository;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.OpenRunProductRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                new IllegalArgumentException()
        );

        product.updateCurrentQuantity(openRunProductRedisRepository.getCurrentQuantityCount(productId));

        Order order = Order.builder()
                .member(member)
                .product(product)
                .count(count)
                .totalPrice(product.getPrice() * count)
                .build();

        orderRepository.save(order);

        System.out.println("성공");
    }

}
