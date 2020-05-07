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
import com.synectiks.fee.domain.DueDate;
import com.synectiks.fee.repository.DueDateRepository;
import com.synectiks.fee.repository.search.DueDateSearchRepository;
import com.synectiks.fee.service.DueDateService;
import com.synectiks.fee.service.dto.DueDateDTO;
import com.synectiks.fee.service.mapper.DueDateMapper;
import com.synectiks.fee.web.rest.errors.ExceptionTranslator;

/**
 * Integration tests for the {@Link DueDateResource} REST controller.
 */
@SpringBootTest(classes = FeeApp.class)
public class DueDateResourceIT {

    private static final String DEFAULT_PAYMENT_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_METHOD = "BBBBBBBBBB";

    private static final Long DEFAULT_INSTALLMENTS = 1L;
    private static final Long UPDATED_INSTALLMENTS = 2L;

    private static final String DEFAULT_DAY_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DAY_DESC = "BBBBBBBBBB";

    private static final Long DEFAULT_PAYMENT_DAY = 1L;
    private static final Long UPDATED_PAYMENT_DAY = 2L;

    private static final String DEFAULT_FREQUENCY = "AAAAAAAAAA";
    private static final String UPDATED_FREQUENCY = "BBBBBBBBBB";

    private static final Long DEFAULT_BRANCH_ID = 1L;
    private static final Long UPDATED_BRANCH_ID = 2L;

    @Autowired
    private DueDateRepository dueDateRepository;

    @Autowired
    private DueDateMapper dueDateMapper;

    @Autowired
    private DueDateService dueDateService;

    /**
     * This repository is mocked in the com.synectiks.fee.repository.search test package.
     *
     * @see com.synectiks.fee.repository.search.DueDateSearchRepositoryMockConfiguration
     */
    @Autowired
    private DueDateSearchRepository mockDueDateSearchRepository;

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

    private MockMvc restDueDateMockMvc;

    private DueDate dueDate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DueDateResource dueDateResource = new DueDateResource(dueDateService);
        this.restDueDateMockMvc = MockMvcBuilders.standaloneSetup(dueDateResource)
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
    public static DueDate createEntity(EntityManager em) {
        DueDate dueDate = new DueDate()
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .installments(DEFAULT_INSTALLMENTS)
            .dayDesc(DEFAULT_DAY_DESC)
            .paymentDay(DEFAULT_PAYMENT_DAY)
            .frequency(DEFAULT_FREQUENCY)
            .branchId(DEFAULT_BRANCH_ID);
        return dueDate;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DueDate createUpdatedEntity(EntityManager em) {
        DueDate dueDate = new DueDate()
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .installments(UPDATED_INSTALLMENTS)
            .dayDesc(UPDATED_DAY_DESC)
            .paymentDay(UPDATED_PAYMENT_DAY)
            .frequency(UPDATED_FREQUENCY)
            .branchId(UPDATED_BRANCH_ID);
        return dueDate;
    }

    @BeforeEach
    public void initTest() {
        dueDate = createEntity(em);
    }

    @Test
    @Transactional
    public void createDueDate() throws Exception {
        int databaseSizeBeforeCreate = dueDateRepository.findAll().size();

        // Create the DueDate
        DueDateDTO dueDateDTO = dueDateMapper.toDto(dueDate);
        restDueDateMockMvc.perform(post("/api/due-dates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dueDateDTO)))
            .andExpect(status().isCreated());

        // Validate the DueDate in the database
        List<DueDate> dueDateList = dueDateRepository.findAll();
        assertThat(dueDateList).hasSize(databaseSizeBeforeCreate + 1);
        DueDate testDueDate = dueDateList.get(dueDateList.size() - 1);
        assertThat(testDueDate.getPaymentMethod()).isEqualTo(DEFAULT_PAYMENT_METHOD);
        assertThat(testDueDate.getInstallments()).isEqualTo(DEFAULT_INSTALLMENTS);
        assertThat(testDueDate.getDayDesc()).isEqualTo(DEFAULT_DAY_DESC);
        assertThat(testDueDate.getPaymentDay()).isEqualTo(DEFAULT_PAYMENT_DAY);
        assertThat(testDueDate.getFrequency()).isEqualTo(DEFAULT_FREQUENCY);
        assertThat(testDueDate.getBranchId()).isEqualTo(DEFAULT_BRANCH_ID);

        // Validate the DueDate in Elasticsearch
        verify(mockDueDateSearchRepository, times(1)).save(testDueDate);
    }

    @Test
    @Transactional
    public void createDueDateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dueDateRepository.findAll().size();

        // Create the DueDate with an existing ID
        dueDate.setId(1L);
        DueDateDTO dueDateDTO = dueDateMapper.toDto(dueDate);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDueDateMockMvc.perform(post("/api/due-dates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dueDateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DueDate in the database
        List<DueDate> dueDateList = dueDateRepository.findAll();
        assertThat(dueDateList).hasSize(databaseSizeBeforeCreate);

        // Validate the DueDate in Elasticsearch
        verify(mockDueDateSearchRepository, times(0)).save(dueDate);
    }


    @Test
    @Transactional
    public void getAllDueDates() throws Exception {
        // Initialize the database
        dueDateRepository.saveAndFlush(dueDate);

        // Get all the dueDateList
        restDueDateMockMvc.perform(get("/api/due-dates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dueDate.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].installments").value(hasItem(DEFAULT_INSTALLMENTS.intValue())))
            .andExpect(jsonPath("$.[*].dayDesc").value(hasItem(DEFAULT_DAY_DESC.toString())))
            .andExpect(jsonPath("$.[*].paymentDay").value(hasItem(DEFAULT_PAYMENT_DAY.intValue())))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(DEFAULT_FREQUENCY.toString())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getDueDate() throws Exception {
        // Initialize the database
        dueDateRepository.saveAndFlush(dueDate);

        // Get the dueDate
        restDueDateMockMvc.perform(get("/api/due-dates/{id}", dueDate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dueDate.getId().intValue()))
            .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD.toString()))
            .andExpect(jsonPath("$.installments").value(DEFAULT_INSTALLMENTS.intValue()))
            .andExpect(jsonPath("$.dayDesc").value(DEFAULT_DAY_DESC.toString()))
            .andExpect(jsonPath("$.paymentDay").value(DEFAULT_PAYMENT_DAY.intValue()))
            .andExpect(jsonPath("$.frequency").value(DEFAULT_FREQUENCY.toString()))
            .andExpect(jsonPath("$.branchId").value(DEFAULT_BRANCH_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDueDate() throws Exception {
        // Get the dueDate
        restDueDateMockMvc.perform(get("/api/due-dates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDueDate() throws Exception {
        // Initialize the database
        dueDateRepository.saveAndFlush(dueDate);

        int databaseSizeBeforeUpdate = dueDateRepository.findAll().size();

        // Update the dueDate
        DueDate updatedDueDate = dueDateRepository.findById(dueDate.getId()).get();
        // Disconnect from session so that the updates on updatedDueDate are not directly saved in db
        em.detach(updatedDueDate);
        updatedDueDate
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .installments(UPDATED_INSTALLMENTS)
            .dayDesc(UPDATED_DAY_DESC)
            .paymentDay(UPDATED_PAYMENT_DAY)
            .frequency(UPDATED_FREQUENCY)
            .branchId(UPDATED_BRANCH_ID);
        DueDateDTO dueDateDTO = dueDateMapper.toDto(updatedDueDate);

        restDueDateMockMvc.perform(put("/api/due-dates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dueDateDTO)))
            .andExpect(status().isOk());

        // Validate the DueDate in the database
        List<DueDate> dueDateList = dueDateRepository.findAll();
        assertThat(dueDateList).hasSize(databaseSizeBeforeUpdate);
        DueDate testDueDate = dueDateList.get(dueDateList.size() - 1);
        assertThat(testDueDate.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
        assertThat(testDueDate.getInstallments()).isEqualTo(UPDATED_INSTALLMENTS);
        assertThat(testDueDate.getDayDesc()).isEqualTo(UPDATED_DAY_DESC);
        assertThat(testDueDate.getPaymentDay()).isEqualTo(UPDATED_PAYMENT_DAY);
        assertThat(testDueDate.getFrequency()).isEqualTo(UPDATED_FREQUENCY);
        assertThat(testDueDate.getBranchId()).isEqualTo(UPDATED_BRANCH_ID);

        // Validate the DueDate in Elasticsearch
        verify(mockDueDateSearchRepository, times(1)).save(testDueDate);
    }

    @Test
    @Transactional
    public void updateNonExistingDueDate() throws Exception {
        int databaseSizeBeforeUpdate = dueDateRepository.findAll().size();

        // Create the DueDate
        DueDateDTO dueDateDTO = dueDateMapper.toDto(dueDate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDueDateMockMvc.perform(put("/api/due-dates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dueDateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DueDate in the database
        List<DueDate> dueDateList = dueDateRepository.findAll();
        assertThat(dueDateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DueDate in Elasticsearch
        verify(mockDueDateSearchRepository, times(0)).save(dueDate);
    }

    @Test
    @Transactional
    public void deleteDueDate() throws Exception {
        // Initialize the database
        dueDateRepository.saveAndFlush(dueDate);

        int databaseSizeBeforeDelete = dueDateRepository.findAll().size();

        // Delete the dueDate
        restDueDateMockMvc.perform(delete("/api/due-dates/{id}", dueDate.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DueDate> dueDateList = dueDateRepository.findAll();
        assertThat(dueDateList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the DueDate in Elasticsearch
        verify(mockDueDateSearchRepository, times(1)).deleteById(dueDate.getId());
    }

    @Test
    @Transactional
    public void searchDueDate() throws Exception {
        // Initialize the database
        dueDateRepository.saveAndFlush(dueDate);
//        when(mockDueDateSearchRepository.search(queryStringQuery("id:" + dueDate.getId())))
//            .thenReturn(Collections.singletonList(dueDate));
        // Search the dueDate
        restDueDateMockMvc.perform(get("/api/_search/due-dates?query=id:" + dueDate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dueDate.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD)))
            .andExpect(jsonPath("$.[*].installments").value(hasItem(DEFAULT_INSTALLMENTS.intValue())))
            .andExpect(jsonPath("$.[*].dayDesc").value(hasItem(DEFAULT_DAY_DESC)))
            .andExpect(jsonPath("$.[*].paymentDay").value(hasItem(DEFAULT_PAYMENT_DAY.intValue())))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(DEFAULT_FREQUENCY)))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DueDate.class);
        DueDate dueDate1 = new DueDate();
        dueDate1.setId(1L);
        DueDate dueDate2 = new DueDate();
        dueDate2.setId(dueDate1.getId());
        assertThat(dueDate1).isEqualTo(dueDate2);
        dueDate2.setId(2L);
        assertThat(dueDate1).isNotEqualTo(dueDate2);
        dueDate1.setId(null);
        assertThat(dueDate1).isNotEqualTo(dueDate2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DueDateDTO.class);
        DueDateDTO dueDateDTO1 = new DueDateDTO();
        dueDateDTO1.setId(1L);
        DueDateDTO dueDateDTO2 = new DueDateDTO();
        assertThat(dueDateDTO1).isNotEqualTo(dueDateDTO2);
        dueDateDTO2.setId(dueDateDTO1.getId());
        assertThat(dueDateDTO1).isEqualTo(dueDateDTO2);
        dueDateDTO2.setId(2L);
        assertThat(dueDateDTO1).isNotEqualTo(dueDateDTO2);
        dueDateDTO1.setId(null);
        assertThat(dueDateDTO1).isNotEqualTo(dueDateDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(dueDateMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(dueDateMapper.fromId(null)).isNull();
    }
}
