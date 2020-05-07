package com.synectiks.fee.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synectiks.fee.domain.PaymentRemainder;
import com.synectiks.fee.repository.PaymentRemainderRepository;
import com.synectiks.fee.repository.search.PaymentRemainderSearchRepository;
import com.synectiks.fee.service.PaymentRemainderService;
import com.synectiks.fee.service.dto.PaymentRemainderDTO;
import com.synectiks.fee.service.mapper.PaymentRemainderMapper;

/**
 * Service Implementation for managing {@link PaymentRemainder}.
 */
@Service
@Transactional
public class PaymentRemainderServiceImpl implements PaymentRemainderService {

    private final Logger log = LoggerFactory.getLogger(PaymentRemainderServiceImpl.class);

    private final PaymentRemainderRepository paymentRemainderRepository;

    private final PaymentRemainderMapper paymentRemainderMapper;

    private final PaymentRemainderSearchRepository paymentRemainderSearchRepository;

    public PaymentRemainderServiceImpl(PaymentRemainderRepository paymentRemainderRepository, PaymentRemainderMapper paymentRemainderMapper, PaymentRemainderSearchRepository paymentRemainderSearchRepository) {
        this.paymentRemainderRepository = paymentRemainderRepository;
        this.paymentRemainderMapper = paymentRemainderMapper;
        this.paymentRemainderSearchRepository = paymentRemainderSearchRepository;
    }

    /**
     * Save a paymentRemainder.
     *
     * @param paymentRemainderDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public PaymentRemainderDTO save(PaymentRemainderDTO paymentRemainderDTO) {
        log.debug("Request to save PaymentRemainder : {}", paymentRemainderDTO);
        PaymentRemainder paymentRemainder = paymentRemainderMapper.toEntity(paymentRemainderDTO);
        paymentRemainder = paymentRemainderRepository.save(paymentRemainder);
        PaymentRemainderDTO result = paymentRemainderMapper.toDto(paymentRemainder);
        paymentRemainderSearchRepository.save(paymentRemainder);
        return result;
    }

    /**
     * Get all the paymentRemainders.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentRemainderDTO> findAll() {
        log.debug("Request to get all PaymentRemainders");
        return paymentRemainderRepository.findAll().stream()
            .map(paymentRemainderMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one paymentRemainder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentRemainderDTO> findOne(Long id) {
        log.debug("Request to get PaymentRemainder : {}", id);
        return paymentRemainderRepository.findById(id)
            .map(paymentRemainderMapper::toDto);
    }

    /**
     * Delete the paymentRemainder by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete PaymentRemainder : {}", id);
        paymentRemainderRepository.deleteById(id);
        paymentRemainderSearchRepository.deleteById(id);
    }

    /**
     * Search for the paymentRemainder corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentRemainderDTO> search(String query) {
        log.debug("Request to search PaymentRemainders for query {}", query);
        return null;
    }
}
