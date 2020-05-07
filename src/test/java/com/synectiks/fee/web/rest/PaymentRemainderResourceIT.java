package com.synectiks.fee.web.rest;

import static com.synectiks.fee.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import com.synectiks.fee.FeeApp;
import com.synectiks.fee.domain.PaymentRemainder;
import com.synectiks.fee.repository.PaymentRemainderRepository;
import com.synectiks.fee.repository.search.PaymentRemainderSearchRepository;
import com.synectiks.fee.service.PaymentRemainderService;
import com.synectiks.fee.service.dto.PaymentRemainderDTO;
import com.synectiks.fee.service.mapper.PaymentRemainderMapper;
import com.synectiks.fee.web.rest.errors.ExceptionTranslator;

/**
 * Integration tests for the {@Link PaymentRemainderResource} REST controller.
 */
@SpringBootTest(classes = FeeApp.class)
public class PaymentRemainderResourceIT {

    private static final String DEFAULT_IS_AUTO_REMAINDER = "AAAAAAAAAA";
    private static final String UPDATED_IS_AUTO_REMAINDER = "BBBBBBBBBB";

    private static final String DEFAULT_IS_FIRST_PAYMENT_REMAINDER = "AAAAAAAAAA";
    private static final String UPDATED_IS_FIRST_PAYMENT_REMAINDER = "BBBBBBBBBB";

    private static final Long DEFAULT_FIRST_PAYMENT_REMAINDER_DAYS = 1L;
    private static final Long UPDATED_FIRST_PAYMENT_REMAINDER_DAYS = 2L;

    private static final String DEFAULT_IS_SECOND_PAYMENT_REMAINDER = "AAAAAAAAAA";
    private static final String UPDATED_IS_SECOND_PAYMENT_REMAINDER = "BBBBBBBBBB";

    private static final Long DEFAULT_SECOND_PAYMENT_REMAINDER_DAYS = 1L;
    private static final Long UPDATED_SECOND_PAYMENT_REMAINDER_DAYS = 2L;

    private static final String DEFAULT_IS_OVER_DUE_PAYMENT_REMAINDER = "AAAAAAAAAA";
    private static final String UPDATED_IS_OVER_DUE_PAYMENT_REMAINDER = "BBBBBBBBBB";

    private static final String DEFAULT_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID = "AAAAAAAAAA";
    private static final String UPDATED_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID = "BBBBBBBBBB";

    private static final Long DEFAULT_OVER_DUE_PAYMENT_REMAINDER_DAYS = 1L;
    private static final Long UPDATED_OVER_DUE_PAYMENT_REMAINDER_DAYS = 2L;

    private static final String DEFAULT_IS_REMAINDER_RECIPIENTS = "AAAAAAAAAA";
    private static final String UPDATED_IS_REMAINDER_RECIPIENTS = "BBBBBBBBBB";

    private static final String DEFAULT_REMAINDER_RECIPIENTS = "AAAAAAAAAA";
    private static final String UPDATED_REMAINDER_RECIPIENTS = "BBBBBBBBBB";

    private static final Long DEFAULT_BRANCH_ID = 1L;
    private static final Long UPDATED_BRANCH_ID = 2L;

    @Autowired
    private PaymentRemainderRepository paymentRemainderRepository;

    @Autowired
    private PaymentRemainderMapper paymentRemainderMapper;

    @Autowired
    private PaymentRemainderService paymentRemainderService;

    /**
     * This repository is mocked in the com.synectiks.fee.repository.search test package.
     *
     * @see com.synectiks.fee.repository.search.PaymentRemainderSearchRepositoryMockConfiguration
     */
    @Autowired
    private PaymentRemainderSearchRepository mockPaymentRemainderSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPaymentRemainderMockMvc;

    private PaymentRemainder paymentRemainder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PaymentRemainderResource paymentRemainderResource = new PaymentRemainderResource(paymentRemainderService);
        this.restPaymentRemainderMockMvc = MockMvcBuilders.standaloneSetup(paymentRemainderResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentRemainder createEntity(EntityManager em) {
        PaymentRemainder paymentRemainder = new PaymentRemainder()
            .isAutoRemainder(DEFAULT_IS_AUTO_REMAINDER)
            .isFirstPaymentRemainder(DEFAULT_IS_FIRST_PAYMENT_REMAINDER)
            .firstPaymentRemainderDays(DEFAULT_FIRST_PAYMENT_REMAINDER_DAYS)
            .isSecondPaymentRemainder(DEFAULT_IS_SECOND_PAYMENT_REMAINDER)
            .secondPaymentRemainderDays(DEFAULT_SECOND_PAYMENT_REMAINDER_DAYS)
            .isOverDuePaymentRemainder(DEFAULT_IS_OVER_DUE_PAYMENT_REMAINDER)
            .overDuePaymentRemainderAfterDueDateOrUntilPaid(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID)
            .overDuePaymentRemainderDays(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_DAYS)
            .isRemainderRecipients(DEFAULT_IS_REMAINDER_RECIPIENTS)
            .remainderRecipients(DEFAULT_REMAINDER_RECIPIENTS)
            .branchId(DEFAULT_BRANCH_ID);
        return paymentRemainder;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentRemainder createUpdatedEntity(EntityManager em) {
        PaymentRemainder paymentRemainder = new PaymentRemainder()
            .isAutoRemainder(UPDATED_IS_AUTO_REMAINDER)
            .isFirstPaymentRemainder(UPDATED_IS_FIRST_PAYMENT_REMAINDER)
            .firstPaymentRemainderDays(UPDATED_FIRST_PAYMENT_REMAINDER_DAYS)
            .isSecondPaymentRemainder(UPDATED_IS_SECOND_PAYMENT_REMAINDER)
            .secondPaymentRemainderDays(UPDATED_SECOND_PAYMENT_REMAINDER_DAYS)
            .isOverDuePaymentRemainder(UPDATED_IS_OVER_DUE_PAYMENT_REMAINDER)
            .overDuePaymentRemainderAfterDueDateOrUntilPaid(UPDATED_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID)
            .overDuePaymentRemainderDays(UPDATED_OVER_DUE_PAYMENT_REMAINDER_DAYS)
            .isRemainderRecipients(UPDATED_IS_REMAINDER_RECIPIENTS)
            .remainderRecipients(UPDATED_REMAINDER_RECIPIENTS)
            .branchId(UPDATED_BRANCH_ID);
        return paymentRemainder;
    }

    @BeforeEach
    public void initTest() {
        paymentRemainder = createEntity(em);
    }

    @Test
    @Transactional
    public void createPaymentRemainder() throws Exception {
        int databaseSizeBeforeCreate = paymentRemainderRepository.findAll().size();

        // Create the PaymentRemainder
        PaymentRemainderDTO paymentRemainderDTO = paymentRemainderMapper.toDto(paymentRemainder);
        restPaymentRemainderMockMvc.perform(post("/api/payment-remainders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentRemainderDTO)))
            .andExpect(status().isCreated());

        // Validate the PaymentRemainder in the database
        List<PaymentRemainder> paymentRemainderList = paymentRemainderRepository.findAll();
        assertThat(paymentRemainderList).hasSize(databaseSizeBeforeCreate + 1);
        PaymentRemainder testPaymentRemainder = paymentRemainderList.get(paymentRemainderList.size() - 1);
        assertThat(testPaymentRemainder.getIsAutoRemainder()).isEqualTo(DEFAULT_IS_AUTO_REMAINDER);
        assertThat(testPaymentRemainder.getIsFirstPaymentRemainder()).isEqualTo(DEFAULT_IS_FIRST_PAYMENT_REMAINDER);
        assertThat(testPaymentRemainder.getFirstPaymentRemainderDays()).isEqualTo(DEFAULT_FIRST_PAYMENT_REMAINDER_DAYS);
        assertThat(testPaymentRemainder.getIsSecondPaymentRemainder()).isEqualTo(DEFAULT_IS_SECOND_PAYMENT_REMAINDER);
        assertThat(testPaymentRemainder.getSecondPaymentRemainderDays()).isEqualTo(DEFAULT_SECOND_PAYMENT_REMAINDER_DAYS);
        assertThat(testPaymentRemainder.getIsOverDuePaymentRemainder()).isEqualTo(DEFAULT_IS_OVER_DUE_PAYMENT_REMAINDER);
        assertThat(testPaymentRemainder.getOverDuePaymentRemainderAfterDueDateOrUntilPaid()).isEqualTo(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID);
        assertThat(testPaymentRemainder.getOverDuePaymentRemainderDays()).isEqualTo(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_DAYS);
        assertThat(testPaymentRemainder.getIsRemainderRecipients()).isEqualTo(DEFAULT_IS_REMAINDER_RECIPIENTS);
        assertThat(testPaymentRemainder.getRemainderRecipients()).isEqualTo(DEFAULT_REMAINDER_RECIPIENTS);
        assertThat(testPaymentRemainder.getBranchId()).isEqualTo(DEFAULT_BRANCH_ID);

        // Validate the PaymentRemainder in Elasticsearch
        verify(mockPaymentRemainderSearchRepository, times(1)).save(testPaymentRemainder);
    }

    @Test
    @Transactional
    public void createPaymentRemainderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = paymentRemainderRepository.findAll().size();

        // Create the PaymentRemainder with an existing ID
        paymentRemainder.setId(1L);
        PaymentRemainderDTO paymentRemainderDTO = paymentRemainderMapper.toDto(paymentRemainder);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentRemainderMockMvc.perform(post("/api/payment-remainders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentRemainderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentRemainder in the database
        List<PaymentRemainder> paymentRemainderList = paymentRemainderRepository.findAll();
        assertThat(paymentRemainderList).hasSize(databaseSizeBeforeCreate);

        // Validate the PaymentRemainder in Elasticsearch
        verify(mockPaymentRemainderSearchRepository, times(0)).save(paymentRemainder);
    }


    @Test
    @Transactional
    public void getAllPaymentRemainders() throws Exception {
        // Initialize the database
        paymentRemainderRepository.saveAndFlush(paymentRemainder);

        // Get all the paymentRemainderList
        restPaymentRemainderMockMvc.perform(get("/api/payment-remainders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentRemainder.getId().intValue())))
            .andExpect(jsonPath("$.[*].isAutoRemainder").value(hasItem(DEFAULT_IS_AUTO_REMAINDER.toString())))
            .andExpect(jsonPath("$.[*].isFirstPaymentRemainder").value(hasItem(DEFAULT_IS_FIRST_PAYMENT_REMAINDER.toString())))
            .andExpect(jsonPath("$.[*].firstPaymentRemainderDays").value(hasItem(DEFAULT_FIRST_PAYMENT_REMAINDER_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].isSecondPaymentRemainder").value(hasItem(DEFAULT_IS_SECOND_PAYMENT_REMAINDER.toString())))
            .andExpect(jsonPath("$.[*].secondPaymentRemainderDays").value(hasItem(DEFAULT_SECOND_PAYMENT_REMAINDER_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].isOverDuePaymentRemainder").value(hasItem(DEFAULT_IS_OVER_DUE_PAYMENT_REMAINDER.toString())))
            .andExpect(jsonPath("$.[*].overDuePaymentRemainderAfterDueDateOrUntilPaid").value(hasItem(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID.toString())))
            .andExpect(jsonPath("$.[*].overDuePaymentRemainderDays").value(hasItem(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].isRemainderRecipients").value(hasItem(DEFAULT_IS_REMAINDER_RECIPIENTS.toString())))
            .andExpect(jsonPath("$.[*].remainderRecipients").value(hasItem(DEFAULT_REMAINDER_RECIPIENTS.toString())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getPaymentRemainder() throws Exception {
        // Initialize the database
        paymentRemainderRepository.saveAndFlush(paymentRemainder);

        // Get the paymentRemainder
        restPaymentRemainderMockMvc.perform(get("/api/payment-remainders/{id}", paymentRemainder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(paymentRemainder.getId().intValue()))
            .andExpect(jsonPath("$.isAutoRemainder").value(DEFAULT_IS_AUTO_REMAINDER.toString()))
            .andExpect(jsonPath("$.isFirstPaymentRemainder").value(DEFAULT_IS_FIRST_PAYMENT_REMAINDER.toString()))
            .andExpect(jsonPath("$.firstPaymentRemainderDays").value(DEFAULT_FIRST_PAYMENT_REMAINDER_DAYS.intValue()))
            .andExpect(jsonPath("$.isSecondPaymentRemainder").value(DEFAULT_IS_SECOND_PAYMENT_REMAINDER.toString()))
            .andExpect(jsonPath("$.secondPaymentRemainderDays").value(DEFAULT_SECOND_PAYMENT_REMAINDER_DAYS.intValue()))
            .andExpect(jsonPath("$.isOverDuePaymentRemainder").value(DEFAULT_IS_OVER_DUE_PAYMENT_REMAINDER.toString()))
            .andExpect(jsonPath("$.overDuePaymentRemainderAfterDueDateOrUntilPaid").value(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID.toString()))
            .andExpect(jsonPath("$.overDuePaymentRemainderDays").value(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_DAYS.intValue()))
            .andExpect(jsonPath("$.isRemainderRecipients").value(DEFAULT_IS_REMAINDER_RECIPIENTS.toString()))
            .andExpect(jsonPath("$.remainderRecipients").value(DEFAULT_REMAINDER_RECIPIENTS.toString()))
            .andExpect(jsonPath("$.branchId").value(DEFAULT_BRANCH_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPaymentRemainder() throws Exception {
        // Get the paymentRemainder
        restPaymentRemainderMockMvc.perform(get("/api/payment-remainders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePaymentRemainder() throws Exception {
        // Initialize the database
        paymentRemainderRepository.saveAndFlush(paymentRemainder);

        int databaseSizeBeforeUpdate = paymentRemainderRepository.findAll().size();

        // Update the paymentRemainder
        PaymentRemainder updatedPaymentRemainder = paymentRemainderRepository.findById(paymentRemainder.getId()).get();
        // Disconnect from session so that the updates on updatedPaymentRemainder are not directly saved in db
        em.detach(updatedPaymentRemainder);
        updatedPaymentRemainder
            .isAutoRemainder(UPDATED_IS_AUTO_REMAINDER)
            .isFirstPaymentRemainder(UPDATED_IS_FIRST_PAYMENT_REMAINDER)
            .firstPaymentRemainderDays(UPDATED_FIRST_PAYMENT_REMAINDER_DAYS)
            .isSecondPaymentRemainder(UPDATED_IS_SECOND_PAYMENT_REMAINDER)
            .secondPaymentRemainderDays(UPDATED_SECOND_PAYMENT_REMAINDER_DAYS)
            .isOverDuePaymentRemainder(UPDATED_IS_OVER_DUE_PAYMENT_REMAINDER)
            .overDuePaymentRemainderAfterDueDateOrUntilPaid(UPDATED_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID)
            .overDuePaymentRemainderDays(UPDATED_OVER_DUE_PAYMENT_REMAINDER_DAYS)
            .isRemainderRecipients(UPDATED_IS_REMAINDER_RECIPIENTS)
            .remainderRecipients(UPDATED_REMAINDER_RECIPIENTS)
            .branchId(UPDATED_BRANCH_ID);
        PaymentRemainderDTO paymentRemainderDTO = paymentRemainderMapper.toDto(updatedPaymentRemainder);

        restPaymentRemainderMockMvc.perform(put("/api/payment-remainders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentRemainderDTO)))
            .andExpect(status().isOk());

        // Validate the PaymentRemainder in the database
        List<PaymentRemainder> paymentRemainderList = paymentRemainderRepository.findAll();
        assertThat(paymentRemainderList).hasSize(databaseSizeBeforeUpdate);
        PaymentRemainder testPaymentRemainder = paymentRemainderList.get(paymentRemainderList.size() - 1);
        assertThat(testPaymentRemainder.getIsAutoRemainder()).isEqualTo(UPDATED_IS_AUTO_REMAINDER);
        assertThat(testPaymentRemainder.getIsFirstPaymentRemainder()).isEqualTo(UPDATED_IS_FIRST_PAYMENT_REMAINDER);
        assertThat(testPaymentRemainder.getFirstPaymentRemainderDays()).isEqualTo(UPDATED_FIRST_PAYMENT_REMAINDER_DAYS);
        assertThat(testPaymentRemainder.getIsSecondPaymentRemainder()).isEqualTo(UPDATED_IS_SECOND_PAYMENT_REMAINDER);
        assertThat(testPaymentRemainder.getSecondPaymentRemainderDays()).isEqualTo(UPDATED_SECOND_PAYMENT_REMAINDER_DAYS);
        assertThat(testPaymentRemainder.getIsOverDuePaymentRemainder()).isEqualTo(UPDATED_IS_OVER_DUE_PAYMENT_REMAINDER);
        assertThat(testPaymentRemainder.getOverDuePaymentRemainderAfterDueDateOrUntilPaid()).isEqualTo(UPDATED_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID);
        assertThat(testPaymentRemainder.getOverDuePaymentRemainderDays()).isEqualTo(UPDATED_OVER_DUE_PAYMENT_REMAINDER_DAYS);
        assertThat(testPaymentRemainder.getIsRemainderRecipients()).isEqualTo(UPDATED_IS_REMAINDER_RECIPIENTS);
        assertThat(testPaymentRemainder.getRemainderRecipients()).isEqualTo(UPDATED_REMAINDER_RECIPIENTS);
        assertThat(testPaymentRemainder.getBranchId()).isEqualTo(UPDATED_BRANCH_ID);

        // Validate the PaymentRemainder in Elasticsearch
        verify(mockPaymentRemainderSearchRepository, times(1)).save(testPaymentRemainder);
    }

    @Test
    @Transactional
    public void updateNonExistingPaymentRemainder() throws Exception {
        int databaseSizeBeforeUpdate = paymentRemainderRepository.findAll().size();

        // Create the PaymentRemainder
        PaymentRemainderDTO paymentRemainderDTO = paymentRemainderMapper.toDto(paymentRemainder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentRemainderMockMvc.perform(put("/api/payment-remainders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paymentRemainderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaymentRemainder in the database
        List<PaymentRemainder> paymentRemainderList = paymentRemainderRepository.findAll();
        assertThat(paymentRemainderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PaymentRemainder in Elasticsearch
        verify(mockPaymentRemainderSearchRepository, times(0)).save(paymentRemainder);
    }

    @Test
    @Transactional
    public void deletePaymentRemainder() throws Exception {
        // Initialize the database
        paymentRemainderRepository.saveAndFlush(paymentRemainder);

        int databaseSizeBeforeDelete = paymentRemainderRepository.findAll().size();

        // Delete the paymentRemainder
        restPaymentRemainderMockMvc.perform(delete("/api/payment-remainders/{id}", paymentRemainder.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PaymentRemainder> paymentRemainderList = paymentRemainderRepository.findAll();
        assertThat(paymentRemainderList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PaymentRemainder in Elasticsearch
        verify(mockPaymentRemainderSearchRepository, times(1)).deleteById(paymentRemainder.getId());
    }

    @Test
    @Transactional
    public void searchPaymentRemainder() throws Exception {
        // Initialize the database
        paymentRemainderRepository.saveAndFlush(paymentRemainder);
//        when(mockPaymentRemainderSearchRepository.search(queryStringQuery("id:" + paymentRemainder.getId())))
//            .thenReturn(Collections.singletonList(paymentRemainder));
        // Search the paymentRemainder
        restPaymentRemainderMockMvc.perform(get("/api/_search/payment-remainders?query=id:" + paymentRemainder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentRemainder.getId().intValue())))
            .andExpect(jsonPath("$.[*].isAutoRemainder").value(hasItem(DEFAULT_IS_AUTO_REMAINDER)))
            .andExpect(jsonPath("$.[*].isFirstPaymentRemainder").value(hasItem(DEFAULT_IS_FIRST_PAYMENT_REMAINDER)))
            .andExpect(jsonPath("$.[*].firstPaymentRemainderDays").value(hasItem(DEFAULT_FIRST_PAYMENT_REMAINDER_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].isSecondPaymentRemainder").value(hasItem(DEFAULT_IS_SECOND_PAYMENT_REMAINDER)))
            .andExpect(jsonPath("$.[*].secondPaymentRemainderDays").value(hasItem(DEFAULT_SECOND_PAYMENT_REMAINDER_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].isOverDuePaymentRemainder").value(hasItem(DEFAULT_IS_OVER_DUE_PAYMENT_REMAINDER)))
            .andExpect(jsonPath("$.[*].overDuePaymentRemainderAfterDueDateOrUntilPaid").value(hasItem(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_AFTER_DUE_DATE_OR_UNTIL_PAID)))
            .andExpect(jsonPath("$.[*].overDuePaymentRemainderDays").value(hasItem(DEFAULT_OVER_DUE_PAYMENT_REMAINDER_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].isRemainderRecipients").value(hasItem(DEFAULT_IS_REMAINDER_RECIPIENTS)))
            .andExpect(jsonPath("$.[*].remainderRecipients").value(hasItem(DEFAULT_REMAINDER_RECIPIENTS)))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentRemainder.class);
        PaymentRemainder paymentRemainder1 = new PaymentRemainder();
        paymentRemainder1.setId(1L);
        PaymentRemainder paymentRemainder2 = new PaymentRemainder();
        paymentRemainder2.setId(paymentRemainder1.getId());
        assertThat(paymentRemainder1).isEqualTo(paymentRemainder2);
        paymentRemainder2.setId(2L);
        assertThat(paymentRemainder1).isNotEqualTo(paymentRemainder2);
        paymentRemainder1.setId(null);
        assertThat(paymentRemainder1).isNotEqualTo(paymentRemainder2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentRemainderDTO.class);
        PaymentRemainderDTO paymentRemainderDTO1 = new PaymentRemainderDTO();
        paymentRemainderDTO1.setId(1L);
        PaymentRemainderDTO paymentRemainderDTO2 = new PaymentRemainderDTO();
        assertThat(paymentRemainderDTO1).isNotEqualTo(paymentRemainderDTO2);
        paymentRemainderDTO2.setId(paymentRemainderDTO1.getId());
        assertThat(paymentRemainderDTO1).isEqualTo(paymentRemainderDTO2);
        paymentRemainderDTO2.setId(2L);
        assertThat(paymentRemainderDTO1).isNotEqualTo(paymentRemainderDTO2);
        paymentRemainderDTO1.setId(null);
        assertThat(paymentRemainderDTO1).isNotEqualTo(paymentRemainderDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(paymentRemainderMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(paymentRemainderMapper.fromId(null)).isNull();
    }
}
