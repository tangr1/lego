package com.manbiwang.peatio.matching.repository;

import com.manbiwang.peatio.matching.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {

    List<OrderEntity> findByCurrencyAndStateOrderById(Integer currency, Integer state);
}
