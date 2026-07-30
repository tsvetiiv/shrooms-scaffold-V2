package com.shrooms.scaffold.service.ourWork;

import com.shrooms.scaffold.exception.ourWork.ProjectNotFoundException;
import com.shrooms.scaffold.mapper.ourWork.OurWorkMapper;
import com.shrooms.scaffold.model.dto.ourWork.OurWorkProjectRequest;
import com.shrooms.scaffold.model.entity.ourWork.OurWorkProject;
import com.shrooms.scaffold.repository.ourWork.OurWorkProjectRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OurWorkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OurWorkService.class);
    private final OurWorkProjectRepository ourWorkProjectRepository;

    public OurWorkService(OurWorkProjectRepository ourWorkProjectRepository) {
        this.ourWorkProjectRepository = ourWorkProjectRepository;
    }

    public List<OurWorkProject> findVisibleProjects() {
        return ourWorkProjectRepository.findAllByVisibleTrue();
    }

    public List<OurWorkProject> findAll() {
        return ourWorkProjectRepository.findAll();
    }

    public void createProject(OurWorkProjectRequest request) {
        OurWorkProject newProject = OurWorkMapper.toProjectEntity(request);
        ourWorkProjectRepository.save(newProject);
        LOGGER.info("Our work project created with title {}", newProject.getTitle());
    }

    public OurWorkProjectRequest getProjectForEdit(UUID id) {
        OurWorkProject oldProject = ourWorkProjectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        return OurWorkMapper.toProjectRequest(oldProject);
    }

    public void updateProject(UUID id, @Valid OurWorkProjectRequest request) {
        OurWorkProject project = ourWorkProjectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        OurWorkMapper.updateOurWorkProjectFormRequest(project, request);
        ourWorkProjectRepository.save(project);
        LOGGER.info("Our work project {} updated", id);
    }

    public void hideProject(UUID id) {
        OurWorkProject projectToHide = ourWorkProjectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        projectToHide.setVisible(false);
        ourWorkProjectRepository.save(projectToHide);
        LOGGER.info("Our work project {} hidden", id);
    }

    public void deleteProject(UUID id) {
        OurWorkProject projectToDelete = ourWorkProjectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        ourWorkProjectRepository.delete(projectToDelete);
        LOGGER.info("Our work project {} deleted", id);
    }
}
