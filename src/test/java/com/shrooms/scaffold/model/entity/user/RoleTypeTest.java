package com.shrooms.scaffold.model.entity.user;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTypeTest {

    @Test
    public void roleType_shouldContainExpectedValues() {
        assertEquals(3, RoleType.values().length);

        assertEquals(RoleType.USER, RoleType.valueOf("USER"));
        assertEquals(RoleType.ADMIN, RoleType.valueOf("ADMIN"));
        assertEquals(RoleType.OWNER, RoleType.valueOf("OWNER"));
    }
}
