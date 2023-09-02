package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ReclamationRepository;
import com.mycompany.myapp.service.ReclamationService;
import com.mycompany.myapp.service.dto.ReclamationDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Reclamation}.
 */
@RestController
@RequestMapping("/api")
public class ReclamationResource {

    private final Logger log = LoggerFactory.getLogger(ReclamationResource.class);

    private static final String ENTITY_NAME = "reclamation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReclamationService reclamationService;

    private final ReclamationRepository reclamationRepository;

    public ReclamationResource(ReclamationService reclamationService, ReclamationRepository reclamationRepository) {
        this.reclamationService = reclamationService;
        this.reclamationRepository = reclamationRepository;
    }

    /**
     * {@code POST  /reclamations} : Create a new reclamation.
     *
     * @param reclamationDTO the reclamationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reclamationDTO, or with status {@code 400 (Bad Request)} if the reclamation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/reclamations")
    public ResponseEntity<ReclamationDTO> createReclamation(@RequestBody ReclamationDTO reclamationDTO) throws URISyntaxException {
        log.debug("REST request to save Reclamation : {}", reclamationDTO);
        if (reclamationDTO.getId() != null) {
            throw new BadRequestAlertException("A new reclamation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ReclamationDTO result = reclamationService.save(reclamationDTO);
        return ResponseEntity
            .created(new URI("/api/reclamations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /reclamations/:id} : Updates an existing reclamation.
     *
     * @param id the id of the reclamationDTO to save.
     * @param reclamationDTO the reclamationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reclamationDTO,
     * or with status {@code 400 (Bad Request)} if the reclamationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reclamationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/reclamations/{id}")
    public ResponseEntity<ReclamationDTO> updateReclamation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReclamationDTO reclamationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Reclamation : {}, {}", id, reclamationDTO);
        if (reclamationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reclamationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reclamationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ReclamationDTO result = reclamationService.update(reclamationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reclamationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /reclamations/:id} : Partial updates given fields of an existing reclamation, field will ignore if it is null
     *
     * @param id the id of the reclamationDTO to save.
     * @param reclamationDTO the reclamationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reclamationDTO,
     * or with status {@code 400 (Bad Request)} if the reclamationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the reclamationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the reclamationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/reclamations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReclamationDTO> partialUpdateReclamation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReclamationDTO reclamationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reclamation partially : {}, {}", id, reclamationDTO);
        if (reclamationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reclamationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reclamationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReclamationDTO> result = reclamationService.partialUpdate(reclamationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reclamationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /reclamations} : get all the reclamations.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reclamations in body.
     */
    @GetMapping("/reclamations")
    public ResponseEntity<List<ReclamationDTO>> getAllReclamations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Reclamations");
        Page<ReclamationDTO> page;
        if (eagerload) {
            page = reclamationService.findAllWithEagerRelationships(pageable);
        } else {
            page = reclamationService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reclamations/:id} : get the "id" reclamation.
     *
     * @param id the id of the reclamationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reclamationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/reclamations/{id}")
    public ResponseEntity<ReclamationDTO> getReclamation(@PathVariable Long id) {
        log.debug("REST request to get Reclamation : {}", id);
        Optional<ReclamationDTO> reclamationDTO = reclamationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reclamationDTO);
    }

    /**
     * {@code DELETE  /reclamations/:id} : delete the "id" reclamation.
     *
     * @param id the id of the reclamationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/reclamations/{id}")
    public ResponseEntity<Void> deleteReclamation(@PathVariable Long id) {
        log.debug("REST request to delete Reclamation : {}", id);
        reclamationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
