package com.shrooms.scaffold.repository.order;

import com.shrooms.scaffold.model.entity.order.Order;
import com.shrooms.scaffold.model.entity.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByUserIdOrderByCreatedOnDesc(UUID userId);

    List<Order> findAllByOrderByCreatedOnDesc();

    boolean existsByScaffoldId(UUID scaffoldId);

     int countAllByOrderStatus(OrderStatus orderStatus);
}
