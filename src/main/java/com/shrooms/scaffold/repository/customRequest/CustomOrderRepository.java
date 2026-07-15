package com.shrooms.scaffold.repository.customRequest;

import com.shrooms.scaffold.model.entity.customOrder.CustomOrder;
import com.shrooms.scaffold.model.entity.customOrder.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface CustomOrderRepository extends JpaRepository<CustomOrder, UUID> {

    List<CustomOrder> findAllByUserIdOrderByCreatedOnDesc(UUID userId);

    List<CustomOrder> findAllByOrderByCreatedOnDesc();

    boolean existsByUserIdAndRequestStatus(UUID userId, RequestStatus requestStatus);

    int countAllByRequestStatus(RequestStatus requestStatus);
}
