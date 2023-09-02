package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReclamationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReclamationDTO.class);
        ReclamationDTO reclamationDTO1 = new ReclamationDTO();
        reclamationDTO1.setId(1L);
        ReclamationDTO reclamationDTO2 = new ReclamationDTO();
        assertThat(reclamationDTO1).isNotEqualTo(reclamationDTO2);
        reclamationDTO2.setId(reclamationDTO1.getId());
        assertThat(reclamationDTO1).isEqualTo(reclamationDTO2);
        reclamationDTO2.setId(2L);
        assertThat(reclamationDTO1).isNotEqualTo(reclamationDTO2);
        reclamationDTO1.setId(null);
        assertThat(reclamationDTO1).isNotEqualTo(reclamationDTO2);
    }
}
