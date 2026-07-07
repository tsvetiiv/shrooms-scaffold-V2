package com.shrooms.scaffold.repository.ourWork;

import com.shrooms.scaffold.model.entity.ourWork.OurWorkProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OurWorkProjectRepository extends JpaRepository<OurWorkProject, UUID> {

    List<OurWorkProject> findAllByVisibleTrue();
}
