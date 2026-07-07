package com.shrooms.scaffold.repository.scaffold;

import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScaffoldRepository extends JpaRepository<Scaffold, UUID> {
}
