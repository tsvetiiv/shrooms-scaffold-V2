package com.shrooms.scaffold.service.scaffold;

import com.shrooms.scaffold.exception.scaffold.ScaffoldNotFoundException;
import com.shrooms.scaffold.mapper.scaffold.ScaffoldMapper;
import com.shrooms.scaffold.model.dto.scaffold.ScaffoldRequest;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.repository.order.OrderRepository;
import com.shrooms.scaffold.repository.scaffold.ScaffoldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScaffoldService {

    private final ScaffoldRepository scaffoldRepository;
    private final OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(ScaffoldService.class);

    public ScaffoldService(ScaffoldRepository scaffoldRepository, OrderRepository orderRepository) {
        this.scaffoldRepository = scaffoldRepository;
        this.orderRepository = orderRepository;
    }

    public Scaffold findById(UUID id) {
        return scaffoldRepository.findById(id)
                .orElseThrow(ScaffoldNotFoundException::new);
    }

    @Cacheable("scaffolds")
    public List<Scaffold> findAll() {
        logger.info("Loading from database...");
        return scaffoldRepository.findAll();
    }

    @CacheEvict(value = "scaffolds", allEntries = true)
    public void editScaffold(UUID id, ScaffoldRequest scaffoldRequest) {
        Scaffold scaffold = scaffoldRepository.findById(id)
                .orElseThrow(ScaffoldNotFoundException::new);

        ScaffoldMapper.updateScaffoldFromRequest(scaffold, scaffoldRequest);

        scaffoldRepository.save(scaffold);
    }

    public ScaffoldRequest getScaffoldForEdit(UUID id) {
        Scaffold scaffold = scaffoldRepository.findById(id)
                .orElseThrow(ScaffoldNotFoundException::new);

        return ScaffoldMapper.toScaffoldRequest(scaffold);
    }

    @CacheEvict(value = "scaffolds", allEntries = true)
    public void createScaffold(ScaffoldRequest request) {
        Scaffold newScaffold = ScaffoldMapper.toScaffoldEntity(request);
        scaffoldRepository.save(newScaffold);
    }

    @CacheEvict(value = "scaffolds", allEntries = true)
    public boolean deleteScaffold(UUID id) {
        Scaffold scaffoldForDelete = scaffoldRepository.findById(id)
                .orElseThrow(ScaffoldNotFoundException::new);
        boolean hasOrders = orderRepository.existsByScaffoldId(id);
        if (hasOrders) {
            scaffoldForDelete.setAvailable(false);
            scaffoldRepository.save(scaffoldForDelete);
            return false;
        }
        scaffoldRepository.deleteById(id);
        return true;
    }
}
