package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Demande;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.DemandeRepository;
import com.mycompany.myapp.service.DemandeService;
import com.mycompany.myapp.service.criteria.DemandeCriteria;
import com.mycompany.myapp.service.dto.DemandeDTO;
import com.mycompany.myapp.service.mapper.DemandeMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link DemandeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DemandeResourceIT {

    private static final String DEFAULT_SUJET = "AAAAAAAAAA";
    private static final String UPDATED_SUJET = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/demandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DemandeRepository demandeRepository;

    @Mock
    private DemandeRepository demandeRepositoryMock;

    @Autowired
    private DemandeMapper demandeMapper;

    @Mock
    private DemandeService demandeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDemandeMockMvc;

    private Demande demande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demande createEntity(EntityManager em) {
        Demande demande = new Demande().sujet(DEFAULT_SUJET).description(DEFAULT_DESCRIPTION);
        return demande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demande createUpdatedEntity(EntityManager em) {
        Demande demande = new Demande().sujet(UPDATED_SUJET).description(UPDATED_DESCRIPTION);
        return demande;
    }

    @BeforeEach
    public void initTest() {
        demande = createEntity(em);
    }

    @Test
    @Transactional
    void createDemande() throws Exception {
        int databaseSizeBeforeCreate = demandeRepository.findAll().size();
        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);
        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isCreated());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeCreate + 1);
        Demande testDemande = demandeList.get(demandeList.size() - 1);
        assertThat(testDemande.getSujet()).isEqualTo(DEFAULT_SUJET);
        assertThat(testDemande.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createDemandeWithExistingId() throws Exception {
        // Create the Demande with an existing ID
        demande.setId(1L);
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        int databaseSizeBeforeCreate = demandeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDemandeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDemandes() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demande.getId().intValue())))
            .andExpect(jsonPath("$.[*].sujet").value(hasItem(DEFAULT_SUJET)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDemandesWithEagerRelationshipsIsEnabled() throws Exception {
        when(demandeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDemandeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(demandeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDemandesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(demandeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDemandeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(demandeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get the demande
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL_ID, demande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(demande.getId().intValue()))
            .andExpect(jsonPath("$.sujet").value(DEFAULT_SUJET))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getDemandesByIdFiltering() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        Long id = demande.getId();

        defaultDemandeShouldBeFound("id.equals=" + id);
        defaultDemandeShouldNotBeFound("id.notEquals=" + id);

        defaultDemandeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDemandeShouldNotBeFound("id.greaterThan=" + id);

        defaultDemandeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDemandeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDemandesBySujetIsEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where sujet equals to DEFAULT_SUJET
        defaultDemandeShouldBeFound("sujet.equals=" + DEFAULT_SUJET);

        // Get all the demandeList where sujet equals to UPDATED_SUJET
        defaultDemandeShouldNotBeFound("sujet.equals=" + UPDATED_SUJET);
    }

    @Test
    @Transactional
    void getAllDemandesBySujetIsInShouldWork() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where sujet in DEFAULT_SUJET or UPDATED_SUJET
        defaultDemandeShouldBeFound("sujet.in=" + DEFAULT_SUJET + "," + UPDATED_SUJET);

        // Get all the demandeList where sujet equals to UPDATED_SUJET
        defaultDemandeShouldNotBeFound("sujet.in=" + UPDATED_SUJET);
    }

    @Test
    @Transactional
    void getAllDemandesBySujetIsNullOrNotNull() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where sujet is not null
        defaultDemandeShouldBeFound("sujet.specified=true");

        // Get all the demandeList where sujet is null
        defaultDemandeShouldNotBeFound("sujet.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesBySujetContainsSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where sujet contains DEFAULT_SUJET
        defaultDemandeShouldBeFound("sujet.contains=" + DEFAULT_SUJET);

        // Get all the demandeList where sujet contains UPDATED_SUJET
        defaultDemandeShouldNotBeFound("sujet.contains=" + UPDATED_SUJET);
    }

    @Test
    @Transactional
    void getAllDemandesBySujetNotContainsSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where sujet does not contain DEFAULT_SUJET
        defaultDemandeShouldNotBeFound("sujet.doesNotContain=" + DEFAULT_SUJET);

        // Get all the demandeList where sujet does not contain UPDATED_SUJET
        defaultDemandeShouldBeFound("sujet.doesNotContain=" + UPDATED_SUJET);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description equals to DEFAULT_DESCRIPTION
        defaultDemandeShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the demandeList where description equals to UPDATED_DESCRIPTION
        defaultDemandeShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultDemandeShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the demandeList where description equals to UPDATED_DESCRIPTION
        defaultDemandeShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description is not null
        defaultDemandeShouldBeFound("description.specified=true");

        // Get all the demandeList where description is null
        defaultDemandeShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description contains DEFAULT_DESCRIPTION
        defaultDemandeShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the demandeList where description contains UPDATED_DESCRIPTION
        defaultDemandeShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        // Get all the demandeList where description does not contain DEFAULT_DESCRIPTION
        defaultDemandeShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the demandeList where description does not contain UPDATED_DESCRIPTION
        defaultDemandeShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            demandeRepository.saveAndFlush(demande);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        demande.setUser(user);
        demandeRepository.saveAndFlush(demande);
        Long userId = user.getId();
        // Get all the demandeList where user equals to userId
        defaultDemandeShouldBeFound("userId.equals=" + userId);

        // Get all the demandeList where user equals to (userId + 1)
        defaultDemandeShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDemandeShouldBeFound(String filter) throws Exception {
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demande.getId().intValue())))
            .andExpect(jsonPath("$.[*].sujet").value(hasItem(DEFAULT_SUJET)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDemandeShouldNotBeFound(String filter) throws Exception {
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDemandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDemande() throws Exception {
        // Get the demande
        restDemandeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();

        // Update the demande
        Demande updatedDemande = demandeRepository.findById(demande.getId()).get();
        // Disconnect from session so that the updates on updatedDemande are not directly saved in db
        em.detach(updatedDemande);
        updatedDemande.sujet(UPDATED_SUJET).description(UPDATED_DESCRIPTION);
        DemandeDTO demandeDTO = demandeMapper.toDto(updatedDemande);

        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
        Demande testDemande = demandeList.get(demandeList.size() - 1);
        assertThat(testDemande.getSujet()).isEqualTo(UPDATED_SUJET);
        assertThat(testDemande.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(demandeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDemandeWithPatch() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();

        // Update the demande using partial update
        Demande partialUpdatedDemande = new Demande();
        partialUpdatedDemande.setId(demande.getId());

        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemande.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDemande))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
        Demande testDemande = demandeList.get(demandeList.size() - 1);
        assertThat(testDemande.getSujet()).isEqualTo(DEFAULT_SUJET);
        assertThat(testDemande.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateDemandeWithPatch() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();

        // Update the demande using partial update
        Demande partialUpdatedDemande = new Demande();
        partialUpdatedDemande.setId(demande.getId());

        partialUpdatedDemande.sujet(UPDATED_SUJET).description(UPDATED_DESCRIPTION);

        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemande.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDemande))
            )
            .andExpect(status().isOk());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
        Demande testDemande = demandeList.get(demandeList.size() - 1);
        assertThat(testDemande.getSujet()).isEqualTo(UPDATED_SUJET);
        assertThat(testDemande.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, demandeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDemande() throws Exception {
        int databaseSizeBeforeUpdate = demandeRepository.findAll().size();
        demande.setId(count.incrementAndGet());

        // Create the Demande
        DemandeDTO demandeDTO = demandeMapper.toDto(demande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(demandeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Demande in the database
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDemande() throws Exception {
        // Initialize the database
        demandeRepository.saveAndFlush(demande);

        int databaseSizeBeforeDelete = demandeRepository.findAll().size();

        // Delete the demande
        restDemandeMockMvc
            .perform(delete(ENTITY_API_URL_ID, demande.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Demande> demandeList = demandeRepository.findAll();
        assertThat(demandeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
