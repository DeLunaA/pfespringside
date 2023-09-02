package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReclamationMapperTest {

    private ReclamationMapper reclamationMapper;

    @BeforeEach
    public void setUp() {
        reclamationMapper = new ReclamationMapperImpl();
    }
}
