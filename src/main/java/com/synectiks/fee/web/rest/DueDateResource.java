package com.synectiks.fee.web.rest;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.fee.service.DueDateService;
import com.synectiks.fee.service.dto.DueDateDTO;
import com.synectiks.fee.web.rest.errors.BadRequestAlertException;
import com.synectiks.fee.web.rest.util.HeaderUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.synectiks.fee.domain.DueDate}.
 */
@RestController
@RequestMapping("/api")
public class DueDateResource {

    private final Logger log = LoggerFactory.getLogger(DueDateResource.class);

    private static final String ENTITY_NAME = "feeDueDate";

    private String applicationName;

    private final DueDateService dueDateService;

    public DueDateResource(DueDateService dueDateService) {
        this.dueDateService = dueDateService;
    }

    /**
     * {@code POST  /due-dates} : Create a new dueDate.
     *
     * @param dueDateDTO the dueDateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dueDateDTO, or with status {@code 400 (Bad Request)} if the dueDate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/due-dates")
    public ResponseEntity<DueDateDTO> createDueDate(@RequestBody DueDateDTO dueDateDTO) throws URISyntaxException {
        log.debug("REST request to save DueDate : {}", dueDateDTO);
        if (dueDateDTO.getId() != null) {
            throw new BadRequestAlertException("A new dueDate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DueDateDTO result = dueDateService.save(dueDateDTO);
        return ResponseEntity.created(new URI("/api/due-dates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /due-dates} : Updates an existing dueDate.
     *
     * @param dueDateDTO the dueDateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dueDateDTO,
     * or with status {@code 400 (Bad Request)} if the dueDateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dueDateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/due-dates")
    public ResponseEntity<DueDateDTO> updateDueDate(@RequestBody DueDateDTO dueDateDTO) throws URISyntaxException {
        log.debug("REST request to update DueDate : {}", dueDateDTO);
        if (dueDateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        DueDateDTO result = dueDateService.save(dueDateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dueDateDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /due-dates} : get all the dueDates.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dueDates in body.
     */
    @GetMapping("/due-dates")
    public List<DueDateDTO> getAllDueDates() {
        log.debug("REST request to get all DueDates");
        return dueDateService.findAll();
    }

    /**
     * {@code GET  /due-dates/:id} : get the "id" dueDate.
     *
     * @param id the id of the dueDateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dueDateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/due-dates/{id}")
    public ResponseEntity<DueDateDTO> getDueDate(@PathVariable Long id) {
        log.debug("REST request to get DueDate : {}", id);
        Optional<DueDateDTO> dueDateDTO = dueDateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dueDateDTO);
    }

    /**
     * {@code DELETE  /due-dates/:id} : delete the "id" dueDate.
     *
     * @param id the id of the dueDateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/due-dates/{id}")
    public ResponseEntity<Void> deleteDueDate(@PathVariable Long id) {
        log.debug("REST request to delete DueDate : {}", id);
        dueDateService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/due-dates?query=:query} : search for the dueDate corresponding
     * to the query.
     *
     * @param query the query of the dueDate search.
     * @return the result of the search.
     */
    @GetMapping("/_search/due-dates")
    public List<DueDateDTO> searchDueDates(@RequestParam String query) {
        log.debug("REST request to search DueDates for query {}", query);
        return dueDateService.search(query);
    }

}
