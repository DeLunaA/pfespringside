package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Reclamation;
import com.mycompany.myapp.repository.ReclamationRepository;
import com.mycompany.myapp.service.dto.ReclamationDTO;
import com.mycompany.myapp.service.mapper.ReclamationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Reclamation}.
 */
@Service
@Transactional
public class ReclamationService {

    private final Logger log = LoggerFactory.getLogger(ReclamationService.class);

    private final ReclamationRepository reclamationRepository;

    private final ReclamationMapper reclamationMapper;

    public ReclamationService(ReclamationRepository reclamationRepository, ReclamationMapper reclamationMapper) {
        this.reclamationRepository = reclamationRepository;
        this.reclamationMapper = reclamationMapper;
    }

    /**
     * Save a reclamation.
     *
     * @param reclamationDTO the entity to save.
     * @return the persisted entity.
     */
    public ReclamationDTO save(ReclamationDTO reclamationDTO) {
        log.debug("Request to save Reclamation : {}", reclamationDTO);
        Reclamation reclamation = reclamationMapper.toEntity(reclamationDTO);
        reclamation = reclamationRepository.save(reclamation);
        return reclamationMapper.toDto(reclamation);
    }

    /**
     * Update a reclamation.
     *
     * @param reclamationDTO the entity to save.
     * @return the persisted entity.
     */
    public ReclamationDTO update(ReclamationDTO reclamationDTO) {
        log.debug("Request to update Reclamation : {}", reclamationDTO);
        Reclamation reclamation = reclamationMapper.toEntity(reclamationDTO);
        reclamation = reclamationRepository.save(reclamation);
        return reclamationMapper.toDto(reclamation);
    }

    /**
     * Partially update a reclamation.
     *
     * @param reclamationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReclamationDTO> partialUpdate(ReclamationDTO reclamationDTO) {
        log.debug("Request to partially update Reclamation : {}", reclamationDTO);

        return reclamationRepository
            .findById(reclamationDTO.getId())
            .map(existingReclamation -> {
                reclamationMapper.partialUpdate(existingReclamation, reclamationDTO);

                return existingReclamation;
            })
            .map(reclamationRepository::save)
            .map(reclamationMapper::toDto);
    }

    /**
     * Get all the reclamations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ReclamationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reclamations");
        return reclamationRepository.findAll(pageable).map(reclamationMapper::toDto);
    }

    /**
     * Get all the reclamations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ReclamationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reclamationRepository.findAllWithEagerRelationships(pageable).map(reclamationMapper::toDto);
    }

    /**
     * Get one reclamation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReclamationDTO> findOne(Long id) {
        log.debug("Request to get Reclamation : {}", id);
        return reclamationRepository.findOneWithEagerRelationships(id).map(reclamationMapper::toDto);
    }

    /**
     * Delete the reclamation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Reclamation : {}", id);
        reclamationRepository.deleteById(id);
    }
}
