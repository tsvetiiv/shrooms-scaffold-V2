package com.shrooms.scaffold.model.entity.scaffold;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScaffoldEnumsTest {

    @Test
    public void materialType_shouldContainExpectedValues() {
        assertEquals(2, MaterialType.values().length);

        assertEquals(MaterialType.STEEL, MaterialType.valueOf("STEEL"));
        assertEquals(MaterialType.ALUMINIUM, MaterialType.valueOf("ALUMINIUM"));
    }

    @Test
    public void scaffoldCategory_shouldContainExpectedValues() {
        assertEquals(3, ScaffoldCategory.values().length);

        assertEquals(ScaffoldCategory.FACADE, ScaffoldCategory.valueOf("FACADE"));
        assertEquals(ScaffoldCategory.MOBILE, ScaffoldCategory.valueOf("MOBILE"));
        assertEquals(ScaffoldCategory.ROOM, ScaffoldCategory.valueOf("ROOM"));
    }
}