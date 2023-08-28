package com.project.openrun.product.repository;

import com.project.openrun.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query(value = "UPDATE Product p SET p.currentQuantity = p.currentQuantity + :count where p.id = :id")
    int updateProductQuantity(@Param("count") Integer count, @Param("id") Long id);
}
