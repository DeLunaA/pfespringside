package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Reclamation;
import com.mycompany.myapp.repository.ReclamationRepository;
import com.mycompany.myapp.service.ReclamationService;
import com.mycompany.myapp.service.dto.ReclamationDTO;
import com.mycompany.myapp.service.mapper.ReclamationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReclamationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReclamationResourceIT {

    private static final Instant DEFAULT_CREATED_IN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_IN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reclamations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ReclamationRepository reclamationRepository;

    @Mock
    private ReclamationRepository reclamationRepositoryMock;

    @Autowired
    private ReclamationMapper reclamationMapper;

    @Mock
    private ReclamationService reclamationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReclamationMockMvc;

    private Reclamation reclamation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reclamation createEntity(EntityManager em) {
        Reclamation reclamation = new Reclamation().createdIn(DEFAULT_CREATED_IN);
        return reclamation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Reclamation createUpdatedEntity(EntityManager em) {
        Reclamation reclamation = new Reclamation().createdIn(UPDATED_CREATED_IN);
        return reclamation;
    }

    @BeforeEach
    public void initTest() {
        reclamation = createEntity(em);
    }

    @Test
    @Transactional
    void createReclamation() throws Exception {
        int databaseSizeBeforeCreate = reclamationRepository.findAll().size();
        // Create the Reclamation
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(reclamation);
        restReclamationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reclamationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeCreate + 1);
        Reclamation testReclamation = reclamationList.get(reclamationList.size() - 1);
        assertThat(testReclamation.getCreatedIn()).isEqualTo(DEFAULT_CREATED_IN);
    }

    @Test
    @Transactional
    void createReclamationWithExistingId() throws Exception {
        // Create the Reclamation with an existing ID
        reclamation.setId(1L);
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(reclamation);

        int databaseSizeBeforeCreate = reclamationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReclamationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reclamationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReclamations() throws Exception {
        // Initialize the database
        reclamationRepository.saveAndFlush(reclamation);

        // Get all the reclamationList
        restReclamationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reclamation.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdIn").value(hasItem(DEFAULT_CREATED_IN.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReclamationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(reclamationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReclamationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(reclamationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReclamationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(reclamationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReclamationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(reclamationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReclamation() throws Exception {
        // Initialize the database
        reclamationRepository.saveAndFlush(reclamation);

        // Get the reclamation
        restReclamationMockMvc
            .perform(get(ENTITY_API_URL_ID, reclamation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reclamation.getId().intValue()))
            .andExpect(jsonPath("$.createdIn").value(DEFAULT_CREATED_IN.toString()));
    }

    @Test
    @Transactional
    void getNonExistingReclamation() throws Exception {
        // Get the reclamation
        restReclamationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReclamation() throws Exception {
        // Initialize the database
        reclamationRepository.saveAndFlush(reclamation);

        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();

        // Update the reclamation
        Reclamation updatedReclamation = reclamationRepository.findById(reclamation.getId()).get();
        // Disconnect from session so that the updates on updatedReclamation are not directly saved in db
        em.detach(updatedReclamation);
        updatedReclamation.createdIn(UPDATED_CREATED_IN);
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(updatedReclamation);

        restReclamationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reclamationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reclamationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
        Reclamation testReclamation = reclamationList.get(reclamationList.size() - 1);
        assertThat(testReclamation.getCreatedIn()).isEqualTo(UPDATED_CREATED_IN);
    }

    @Test
    @Transactional
    void putNonExistingReclamation() throws Exception {
        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();
        reclamation.setId(count.incrementAndGet());

        // Create the Reclamation
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(reclamation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReclamationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reclamationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reclamationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReclamation() throws Exception {
        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();
        reclamation.setId(count.incrementAndGet());

        // Create the Reclamation
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(reclamation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReclamationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(reclamationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReclamation() throws Exception {
        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();
        reclamation.setId(count.incrementAndGet());

        // Create the Reclamation
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(reclamation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReclamationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(reclamationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReclamationWithPatch() throws Exception {
        // Initialize the database
        reclamationRepository.saveAndFlush(reclamation);

        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();

        // Update the reclamation using partial update
        Reclamation partialUpdatedReclamation = new Reclamation();
        partialUpdatedReclamation.setId(reclamation.getId());

        restReclamationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReclamation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReclamation))
            )
            .andExpect(status().isOk());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
        Reclamation testReclamation = reclamationList.get(reclamationList.size() - 1);
        assertThat(testReclamation.getCreatedIn()).isEqualTo(DEFAULT_CREATED_IN);
    }

    @Test
    @Transactional
    void fullUpdateReclamationWithPatch() throws Exception {
        // Initialize the database
        reclamationRepository.saveAndFlush(reclamation);

        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();

        // Update the reclamation using partial update
        Reclamation partialUpdatedReclamation = new Reclamation();
        partialUpdatedReclamation.setId(reclamation.getId());

        partialUpdatedReclamation.createdIn(UPDATED_CREATED_IN);

        restReclamationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReclamation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedReclamation))
            )
            .andExpect(status().isOk());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
        Reclamation testReclamation = reclamationList.get(reclamationList.size() - 1);
        assertThat(testReclamation.getCreatedIn()).isEqualTo(UPDATED_CREATED_IN);
    }

    @Test
    @Transactional
    void patchNonExistingReclamation() throws Exception {
        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();
        reclamation.setId(count.incrementAndGet());

        // Create the Reclamation
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(reclamation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReclamationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reclamationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reclamationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReclamation() throws Exception {
        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();
        reclamation.setId(count.incrementAndGet());

        // Create the Reclamation
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(reclamation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReclamationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(reclamationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReclamation() throws Exception {
        int databaseSizeBeforeUpdate = reclamationRepository.findAll().size();
        reclamation.setId(count.incrementAndGet());

        // Create the Reclamation
        ReclamationDTO reclamationDTO = reclamationMapper.toDto(reclamation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReclamationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(reclamationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Reclamation in the database
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReclamation() throws Exception {
        // Initialize the database
        reclamationRepository.saveAndFlush(reclamation);

        int databaseSizeBeforeDelete = reclamationRepository.findAll().size();

        // Delete the reclamation
        restReclamationMockMvc
            .perform(delete(ENTITY_API_URL_ID, reclamation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Reclamation> reclamationList = reclamationRepository.findAll();
        assertThat(reclamationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
