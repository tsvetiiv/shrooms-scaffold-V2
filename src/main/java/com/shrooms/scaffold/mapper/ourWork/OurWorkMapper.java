package com.shrooms.scaffold.mapper.ourWork;

import com.shrooms.scaffold.model.dto.ourWork.OurWorkProjectRequest;
import com.shrooms.scaffold.model.entity.ourWork.OurWorkProject;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class OurWorkMapper {

    public static OurWorkProject toProjectEntity(OurWorkProjectRequest request) {

        if (request == null) {
            return null;
        }

        return OurWorkProject.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .visible(true)
                .build();

    }

    public static OurWorkProjectRequest toProjectRequest(OurWorkProject project) {
        if (project == null) {
            return null;
        }

        return OurWorkProjectRequest.builder()
                .title(project.getTitle())
                .imageUrl(project.getImageUrl())
                .description(project.getDescription())
                .visible(project.isVisible())
                .build();
    }

    public static void updateOurWorkProjectFormRequest(OurWorkProject project, OurWorkProjectRequest request) {
        if (project == null || request == null) {
            return;
        }
        project.setTitle(request.getTitle());
        project.setImageUrl(request.getImageUrl());
        project.setDescription(request.getDescription());
        project.setVisible(request.isVisible());

    }


}
