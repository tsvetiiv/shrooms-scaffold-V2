package com.shrooms.scaffold.mapper.scaffold;

import com.shrooms.scaffold.model.dto.scaffold.ScaffoldRequest;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ScaffoldMapper {

    public static Scaffold toScaffoldEntity(ScaffoldRequest scaffoldRequest) {

        if (scaffoldRequest == null) {
            return null;
        }

        return Scaffold.builder()
                .name(scaffoldRequest.getName())
                .description(scaffoldRequest.getDescription())
                .height(scaffoldRequest.getHeight())
                .width(scaffoldRequest.getWidth())
                .length(scaffoldRequest.getLength())
                .materialType(scaffoldRequest.getMaterialType())
                .scaffoldCategory(scaffoldRequest.getScaffoldCategory())
                .priceForRent(scaffoldRequest.getPriceForRent())
                .priceForSale(scaffoldRequest.getPriceForSale())
                .imageUrl(scaffoldRequest.getImageUrl())
                .available(scaffoldRequest.isAvailable())
                .build();
    }

    public static ScaffoldRequest toScaffoldRequest(Scaffold scaffold) {
        if (scaffold == null) {
            return null;
        }

        return ScaffoldRequest.builder()
                .name(scaffold.getName())
                .description(scaffold.getDescription())
                .height(scaffold.getHeight())
                .width(scaffold.getWidth())
                .length(scaffold.getLength())
                .materialType(scaffold.getMaterialType())
                .scaffoldCategory(scaffold.getScaffoldCategory())
                .priceForRent(scaffold.getPriceForRent())
                .priceForSale(scaffold.getPriceForSale())
                .imageUrl(scaffold.getImageUrl())
                .available(scaffold.isAvailable())
                .build();
    }

    public static void updateScaffoldFromRequest(Scaffold scaffold, ScaffoldRequest request) {

        if (scaffold == null || request == null) {
            return;
        }
        scaffold.setName(request.getName());
        scaffold.setDescription(request.getDescription());
        scaffold.setHeight(request.getHeight());
        scaffold.setWidth(request.getWidth());
        scaffold.setLength(request.getLength());
        scaffold.setMaterialType(request.getMaterialType());
        scaffold.setScaffoldCategory(request.getScaffoldCategory());
        scaffold.setPriceForRent(request.getPriceForRent());
        scaffold.setPriceForSale(request.getPriceForSale());
        scaffold.setImageUrl(request.getImageUrl());
        scaffold.setAvailable(request.isAvailable());

    }
}
