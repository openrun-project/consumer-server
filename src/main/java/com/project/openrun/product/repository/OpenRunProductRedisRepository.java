package com.project.openrun.product.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OpenRunProductRedisRepository {

    public static final String CURRENT_QUANTITY_COUNT_KEY = "CURRENT_QUANTITY_COUNT";

    private final RedisTemplate<String, Integer> productCurrentQuantityCountTemplate;

    public void increaseQuantity(Long subKey, Integer count) {
        productCurrentQuantityCountTemplate.opsForValue().increment(createOrderKey(subKey), count);
    }

    public Integer getCurrentQuantityCount(Long subKey) {
        return productCurrentQuantityCountTemplate.opsForValue().get(createOrderKey(subKey));
    }

    private String createOrderKey(Long subKey){
        return CURRENT_QUANTITY_COUNT_KEY+":"+subKey;
    }

}
