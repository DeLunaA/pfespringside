package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReclamationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reclamation.class);
        Reclamation reclamation1 = new Reclamation();
        reclamation1.setId(1L);
        Reclamation reclamation2 = new Reclamation();
        reclamation2.setId(reclamation1.getId());
        assertThat(reclamation1).isEqualTo(reclamation2);
        reclamation2.setId(2L);
        assertThat(reclamation1).isNotEqualTo(reclamation2);
        reclamation1.setId(null);
        assertThat(reclamation1).isNotEqualTo(reclamation2);
    }
}
