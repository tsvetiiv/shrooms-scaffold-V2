package com.shrooms.scaffold.mapper.scaffold;

import com.shrooms.scaffold.model.dto.scaffold.ScaffoldRequest;
import com.shrooms.scaffold.model.entity.scaffold.MaterialType;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.scaffold.ScaffoldCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ScaffoldMapperTest {

    @Test
    public void toScaffoldEntity_shouldReturnNullWhenRequestIsNull() {
        Scaffold result = ScaffoldMapper.toScaffoldEntity(null);

        assertNull(result);
    }

    @Test
    public void toScaffoldEntity_shouldMapRequestToScaffold() {

        ScaffoldRequest request = ScaffoldRequest.builder()
                .name("Facade")
                .description("Test scaffold")
                .height(2.0)
                .width(3.0)
                .length(4.0)
                .materialType(MaterialType.STEEL)
                .scaffoldCategory(ScaffoldCategory.FACADE)
                .priceForRent(new BigDecimal("100.00"))
                .priceForSale(new BigDecimal("1000.00"))
                .imageUrl("image.png")
                .available(true)
                .build();

        Scaffold result = ScaffoldMapper.toScaffoldEntity(request);

        assertEquals("Facade", result.getName());
        assertEquals("Test scaffold", result.getDescription());
        assertEquals(2.0, result.getHeight());
        assertEquals(MaterialType.STEEL, result.getMaterialType());
        assertTrue(result.isAvailable());
    }

    @Test
    public void toScaffoldRequest_shouldReturnNullWhenScaffoldIsNull() {
        ScaffoldRequest result = ScaffoldMapper.toScaffoldRequest(null);

        assertNull(result);
    }

    @Test
    public void toScaffoldRequest_shouldMapScaffoldToRequest() {
        UUID scaffoldId = UUID.randomUUID();
        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .name("Facade")
                .scaffoldCategory(ScaffoldCategory.FACADE)
                .width(12)
                .height(12)
                .length(12)
                .available(true)
                .priceForRent(new BigDecimal("100.00"))
                .priceForSale(new BigDecimal("1000.00"))
                .imageUrl("image.png")
                .materialType(MaterialType.STEEL)
                .description("Test scaffold")
                .build();

        ScaffoldRequest result = ScaffoldMapper.toScaffoldRequest(scaffold);

        assertEquals(ScaffoldCategory.FACADE, result.getScaffoldCategory());
        assertEquals(12, result.getWidth());
        assertEquals(12, result.getHeight());
        assertEquals(12, result.getLength());
        assertTrue(result.isAvailable());
    }

    @Test
    public void updateScaffoldFromRequest_shouldUpdateScaffold() {

        UUID scaffoldId = UUID.randomUUID();

        Scaffold scaffold = Scaffold.builder()
                .id(scaffoldId)
                .name("Roof")
                .scaffoldCategory(ScaffoldCategory.FACADE)
                .width(12)
                .height(12)
                .length(12)
                .available(true)
                .priceForRent(new BigDecimal("100.00"))
                .priceForSale(new BigDecimal("1000.00"))
                .imageUrl("image.png")
                .materialType(MaterialType.STEEL)
                .description("Test scaffold")
                .build();

        ScaffoldRequest request = ScaffoldRequest.builder()
                .name("Facade")
                .description("Test scaffold request")
                .height(2.0)
                .width(3.0)
                .length(4.0)
                .materialType(MaterialType.ALUMINIUM)
                .scaffoldCategory(ScaffoldCategory.FACADE)
                .priceForRent(new BigDecimal("100.00"))
                .priceForSale(new BigDecimal("1000.00"))
                .imageUrl("image.png")
                .available(true)
                .build();

        ScaffoldMapper.updateScaffoldFromRequest(scaffold, request);

        assertEquals("Facade", scaffold.getName());
        assertEquals("Test scaffold request", scaffold.getDescription());
        assertEquals(2.0, scaffold.getHeight());
        assertEquals(3.0, scaffold.getWidth());
        assertEquals(4.0, scaffold.getLength());
        assertEquals(MaterialType.ALUMINIUM, scaffold.getMaterialType());
    }

    @Test
    public void updateScaffoldFromRequest_shouldDoNothingWhenScaffoldIsNull() {
        ScaffoldRequest request = ScaffoldRequest.builder()
                .name("Facade")
                .build();

        ScaffoldMapper.updateScaffoldFromRequest(null, request);
    }

    @Test
    public void updateScaffoldFromRequest_shouldDoNothingWhenRequestIsNull() {
        Scaffold scaffold = Scaffold.builder()
                .name("Roof")
                .build();

        ScaffoldMapper.updateScaffoldFromRequest(scaffold, null);

        assertEquals("Roof", scaffold.getName());
    }
}
