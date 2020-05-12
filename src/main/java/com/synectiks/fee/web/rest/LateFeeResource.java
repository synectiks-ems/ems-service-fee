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

import com.synectiks.fee.service.LateFeeService;
import com.synectiks.fee.service.dto.LateFeeDTO;
import com.synectiks.fee.web.rest.errors.BadRequestAlertException;
import com.synectiks.fee.web.rest.util.HeaderUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.synectiks.fee.domain.LateFee}.
 */
@RestController
@RequestMapping("/api")
public class LateFeeResource {

    private final Logger log = LoggerFactory.getLogger(LateFeeResource.class);

    private static final String ENTITY_NAME = "feeLateFee";

    private String applicationName;

    private final LateFeeService lateFeeService;

    public LateFeeResource(LateFeeService lateFeeService) {
        this.lateFeeService = lateFeeService;
    }

    /**
     * {@code POST  /late-fees} : Create a new lateFee.
     *
     * @param lateFeeDTO the lateFeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lateFeeDTO, or with status {@code 400 (Bad Request)} if the lateFee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/late-fees")
    public ResponseEntity<LateFeeDTO> createLateFee(@RequestBody LateFeeDTO lateFeeDTO) throws URISyntaxException {
        log.debug("REST request to save LateFee : {}", lateFeeDTO);
        if (lateFeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new lateFee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LateFeeDTO result = lateFeeService.save(lateFeeDTO);
        return ResponseEntity.created(new URI("/api/late-fees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /late-fees} : Updates an existing lateFee.
     *
     * @param lateFeeDTO the lateFeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lateFeeDTO,
     * or with status {@code 400 (Bad Request)} if the lateFeeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lateFeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/late-fees")
    public ResponseEntity<LateFeeDTO> updateLateFee(@RequestBody LateFeeDTO lateFeeDTO) throws URISyntaxException {
        log.debug("REST request to update LateFee : {}", lateFeeDTO);
        if (lateFeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LateFeeDTO result = lateFeeService.save(lateFeeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, lateFeeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /late-fees} : get all the lateFees.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lateFees in body.
     */
    @GetMapping("/late-fees")
    public List<LateFeeDTO> getAllLateFees() {
        log.debug("REST request to get all LateFees");
        return lateFeeService.findAll();
    }

    /**
     * {@code GET  /late-fees/:id} : get the "id" lateFee.
     *
     * @param id the id of the lateFeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lateFeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/late-fees/{id}")
    public ResponseEntity<LateFeeDTO> getLateFee(@PathVariable Long id) {
        log.debug("REST request to get LateFee : {}", id);
        Optional<LateFeeDTO> lateFeeDTO = lateFeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lateFeeDTO);
    }

    /**
     * {@code DELETE  /late-fees/:id} : delete the "id" lateFee.
     *
     * @param id the id of the lateFeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/late-fees/{id}")
    public ResponseEntity<Void> deleteLateFee(@PathVariable Long id) {
        log.debug("REST request to delete LateFee : {}", id);
        lateFeeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/late-fees?query=:query} : search for the lateFee corresponding
     * to the query.
     *
     * @param query the query of the lateFee search.
     * @return the result of the search.
     */
    @GetMapping("/_search/late-fees")
    public List<LateFeeDTO> searchLateFees(@RequestParam String query) {
        log.debug("REST request to search LateFees for query {}", query);
        return lateFeeService.search(query);
    }

}
