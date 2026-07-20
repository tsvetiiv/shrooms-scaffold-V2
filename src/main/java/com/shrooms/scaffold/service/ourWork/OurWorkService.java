package com.shrooms.scaffold.service.ourWork;

import com.shrooms.scaffold.Exception.ourWork.ProjectNotFoundException;
import com.shrooms.scaffold.mapper.ourWork.OurWorkMapper;
import com.shrooms.scaffold.model.dto.ourWork.OurWorkProjectRequest;
import com.shrooms.scaffold.model.entity.ourWork.OurWorkProject;
import com.shrooms.scaffold.repository.ourWork.OurWorkProjectRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OurWorkService {

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
    }

    public void hideProject(UUID id) {
        OurWorkProject projectToHide = ourWorkProjectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        projectToHide.setVisible(false);
        ourWorkProjectRepository.save(projectToHide);
    }

    public void deleteProject(UUID id) {
        OurWorkProject projectToDelete = ourWorkProjectRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        ourWorkProjectRepository.delete(projectToDelete);
    }
}
