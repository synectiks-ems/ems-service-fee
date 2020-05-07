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
import com.synectiks.fee.domain.LateFee;
import com.synectiks.fee.repository.LateFeeRepository;
import com.synectiks.fee.repository.search.LateFeeSearchRepository;
import com.synectiks.fee.service.LateFeeService;
import com.synectiks.fee.service.dto.LateFeeDTO;
import com.synectiks.fee.service.mapper.LateFeeMapper;
import com.synectiks.fee.web.rest.errors.ExceptionTranslator;

/**
 * Integration tests for the {@Link LateFeeResource} REST controller.
 */
@SpringBootTest(classes = FeeApp.class)
public class LateFeeResourceIT {

    private static final String DEFAULT_IS_AUTO_LATE_FEE = "AAAAAAAAAA";
    private static final String UPDATED_IS_AUTO_LATE_FEE = "BBBBBBBBBB";

    private static final Long DEFAULT_LATE_FEE_DAYS = 1L;
    private static final Long UPDATED_LATE_FEE_DAYS = 2L;

    private static final String DEFAULT_CHARGE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CHARGE_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_FIXED_CHARGES = 1L;
    private static final Long UPDATED_FIXED_CHARGES = 2L;

    private static final String DEFAULT_PERCENT_CHARGES = "AAAAAAAAAA";
    private static final String UPDATED_PERCENT_CHARGES = "BBBBBBBBBB";

    private static final String DEFAULT_LATE_FEE_FREQUENCY = "AAAAAAAAAA";
    private static final String UPDATED_LATE_FEE_FREQUENCY = "BBBBBBBBBB";

    private static final Long DEFAULT_LATE_FEE_REPEAT_DAYS = 1L;
    private static final Long UPDATED_LATE_FEE_REPEAT_DAYS = 2L;

    private static final Long DEFAULT_ACADEMIC_YEAR_ID = 1L;
    private static final Long UPDATED_ACADEMIC_YEAR_ID = 2L;

    private static final Long DEFAULT_TERM_ID = 1L;
    private static final Long UPDATED_TERM_ID = 2L;

    private static final Long DEFAULT_BRANCH_ID = 1L;
    private static final Long UPDATED_BRANCH_ID = 2L;

    @Autowired
    private LateFeeRepository lateFeeRepository;

    @Autowired
    private LateFeeMapper lateFeeMapper;

    @Autowired
    private LateFeeService lateFeeService;

    /**
     * This repository is mocked in the com.synectiks.fee.repository.search test package.
     *
     * @see com.synectiks.fee.repository.search.LateFeeSearchRepositoryMockConfiguration
     */
    @Autowired
    private LateFeeSearchRepository mockLateFeeSearchRepository;

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

    private MockMvc restLateFeeMockMvc;

    private LateFee lateFee;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LateFeeResource lateFeeResource = new LateFeeResource(lateFeeService);
        this.restLateFeeMockMvc = MockMvcBuilders.standaloneSetup(lateFeeResource)
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
    public static LateFee createEntity(EntityManager em) {
        LateFee lateFee = new LateFee()
            .isAutoLateFee(DEFAULT_IS_AUTO_LATE_FEE)
            .lateFeeDays(DEFAULT_LATE_FEE_DAYS)
            .chargeType(DEFAULT_CHARGE_TYPE)
            .fixedCharges(DEFAULT_FIXED_CHARGES)
            .percentCharges(DEFAULT_PERCENT_CHARGES)
            .lateFeeFrequency(DEFAULT_LATE_FEE_FREQUENCY)
            .lateFeeRepeatDays(DEFAULT_LATE_FEE_REPEAT_DAYS)
            .academicYearId(DEFAULT_ACADEMIC_YEAR_ID)
            .termId(DEFAULT_TERM_ID)
            .branchId(DEFAULT_BRANCH_ID);
        return lateFee;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LateFee createUpdatedEntity(EntityManager em) {
        LateFee lateFee = new LateFee()
            .isAutoLateFee(UPDATED_IS_AUTO_LATE_FEE)
            .lateFeeDays(UPDATED_LATE_FEE_DAYS)
            .chargeType(UPDATED_CHARGE_TYPE)
            .fixedCharges(UPDATED_FIXED_CHARGES)
            .percentCharges(UPDATED_PERCENT_CHARGES)
            .lateFeeFrequency(UPDATED_LATE_FEE_FREQUENCY)
            .lateFeeRepeatDays(UPDATED_LATE_FEE_REPEAT_DAYS)
            .academicYearId(UPDATED_ACADEMIC_YEAR_ID)
            .termId(UPDATED_TERM_ID)
            .branchId(UPDATED_BRANCH_ID);
        return lateFee;
    }

    @BeforeEach
    public void initTest() {
        lateFee = createEntity(em);
    }

    @Test
    @Transactional
    public void createLateFee() throws Exception {
        int databaseSizeBeforeCreate = lateFeeRepository.findAll().size();

        // Create the LateFee
        LateFeeDTO lateFeeDTO = lateFeeMapper.toDto(lateFee);
        restLateFeeMockMvc.perform(post("/api/late-fees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lateFeeDTO)))
            .andExpect(status().isCreated());

        // Validate the LateFee in the database
        List<LateFee> lateFeeList = lateFeeRepository.findAll();
        assertThat(lateFeeList).hasSize(databaseSizeBeforeCreate + 1);
        LateFee testLateFee = lateFeeList.get(lateFeeList.size() - 1);
        assertThat(testLateFee.getIsAutoLateFee()).isEqualTo(DEFAULT_IS_AUTO_LATE_FEE);
        assertThat(testLateFee.getLateFeeDays()).isEqualTo(DEFAULT_LATE_FEE_DAYS);
        assertThat(testLateFee.getChargeType()).isEqualTo(DEFAULT_CHARGE_TYPE);
        assertThat(testLateFee.getFixedCharges()).isEqualTo(DEFAULT_FIXED_CHARGES);
        assertThat(testLateFee.getPercentCharges()).isEqualTo(DEFAULT_PERCENT_CHARGES);
        assertThat(testLateFee.getLateFeeFrequency()).isEqualTo(DEFAULT_LATE_FEE_FREQUENCY);
        assertThat(testLateFee.getLateFeeRepeatDays()).isEqualTo(DEFAULT_LATE_FEE_REPEAT_DAYS);
        assertThat(testLateFee.getAcademicYearId()).isEqualTo(DEFAULT_ACADEMIC_YEAR_ID);
        assertThat(testLateFee.getTermId()).isEqualTo(DEFAULT_TERM_ID);
        assertThat(testLateFee.getBranchId()).isEqualTo(DEFAULT_BRANCH_ID);

        // Validate the LateFee in Elasticsearch
        verify(mockLateFeeSearchRepository, times(1)).save(testLateFee);
    }

    @Test
    @Transactional
    public void createLateFeeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lateFeeRepository.findAll().size();

        // Create the LateFee with an existing ID
        lateFee.setId(1L);
        LateFeeDTO lateFeeDTO = lateFeeMapper.toDto(lateFee);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLateFeeMockMvc.perform(post("/api/late-fees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lateFeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LateFee in the database
        List<LateFee> lateFeeList = lateFeeRepository.findAll();
        assertThat(lateFeeList).hasSize(databaseSizeBeforeCreate);

        // Validate the LateFee in Elasticsearch
        verify(mockLateFeeSearchRepository, times(0)).save(lateFee);
    }


    @Test
    @Transactional
    public void getAllLateFees() throws Exception {
        // Initialize the database
        lateFeeRepository.saveAndFlush(lateFee);

        // Get all the lateFeeList
        restLateFeeMockMvc.perform(get("/api/late-fees?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lateFee.getId().intValue())))
            .andExpect(jsonPath("$.[*].isAutoLateFee").value(hasItem(DEFAULT_IS_AUTO_LATE_FEE.toString())))
            .andExpect(jsonPath("$.[*].lateFeeDays").value(hasItem(DEFAULT_LATE_FEE_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].chargeType").value(hasItem(DEFAULT_CHARGE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fixedCharges").value(hasItem(DEFAULT_FIXED_CHARGES.intValue())))
            .andExpect(jsonPath("$.[*].percentCharges").value(hasItem(DEFAULT_PERCENT_CHARGES.toString())))
            .andExpect(jsonPath("$.[*].lateFeeFrequency").value(hasItem(DEFAULT_LATE_FEE_FREQUENCY.toString())))
            .andExpect(jsonPath("$.[*].lateFeeRepeatDays").value(hasItem(DEFAULT_LATE_FEE_REPEAT_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].academicYearId").value(hasItem(DEFAULT_ACADEMIC_YEAR_ID.intValue())))
            .andExpect(jsonPath("$.[*].termId").value(hasItem(DEFAULT_TERM_ID.intValue())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getLateFee() throws Exception {
        // Initialize the database
        lateFeeRepository.saveAndFlush(lateFee);

        // Get the lateFee
        restLateFeeMockMvc.perform(get("/api/late-fees/{id}", lateFee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(lateFee.getId().intValue()))
            .andExpect(jsonPath("$.isAutoLateFee").value(DEFAULT_IS_AUTO_LATE_FEE.toString()))
            .andExpect(jsonPath("$.lateFeeDays").value(DEFAULT_LATE_FEE_DAYS.intValue()))
            .andExpect(jsonPath("$.chargeType").value(DEFAULT_CHARGE_TYPE.toString()))
            .andExpect(jsonPath("$.fixedCharges").value(DEFAULT_FIXED_CHARGES.intValue()))
            .andExpect(jsonPath("$.percentCharges").value(DEFAULT_PERCENT_CHARGES.toString()))
            .andExpect(jsonPath("$.lateFeeFrequency").value(DEFAULT_LATE_FEE_FREQUENCY.toString()))
            .andExpect(jsonPath("$.lateFeeRepeatDays").value(DEFAULT_LATE_FEE_REPEAT_DAYS.intValue()))
            .andExpect(jsonPath("$.academicYearId").value(DEFAULT_ACADEMIC_YEAR_ID.intValue()))
            .andExpect(jsonPath("$.termId").value(DEFAULT_TERM_ID.intValue()))
            .andExpect(jsonPath("$.branchId").value(DEFAULT_BRANCH_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingLateFee() throws Exception {
        // Get the lateFee
        restLateFeeMockMvc.perform(get("/api/late-fees/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLateFee() throws Exception {
        // Initialize the database
        lateFeeRepository.saveAndFlush(lateFee);

        int databaseSizeBeforeUpdate = lateFeeRepository.findAll().size();

        // Update the lateFee
        LateFee updatedLateFee = lateFeeRepository.findById(lateFee.getId()).get();
        // Disconnect from session so that the updates on updatedLateFee are not directly saved in db
        em.detach(updatedLateFee);
        updatedLateFee
            .isAutoLateFee(UPDATED_IS_AUTO_LATE_FEE)
            .lateFeeDays(UPDATED_LATE_FEE_DAYS)
            .chargeType(UPDATED_CHARGE_TYPE)
            .fixedCharges(UPDATED_FIXED_CHARGES)
            .percentCharges(UPDATED_PERCENT_CHARGES)
            .lateFeeFrequency(UPDATED_LATE_FEE_FREQUENCY)
            .lateFeeRepeatDays(UPDATED_LATE_FEE_REPEAT_DAYS)
            .academicYearId(UPDATED_ACADEMIC_YEAR_ID)
            .termId(UPDATED_TERM_ID)
            .branchId(UPDATED_BRANCH_ID);
        LateFeeDTO lateFeeDTO = lateFeeMapper.toDto(updatedLateFee);

        restLateFeeMockMvc.perform(put("/api/late-fees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lateFeeDTO)))
            .andExpect(status().isOk());

        // Validate the LateFee in the database
        List<LateFee> lateFeeList = lateFeeRepository.findAll();
        assertThat(lateFeeList).hasSize(databaseSizeBeforeUpdate);
        LateFee testLateFee = lateFeeList.get(lateFeeList.size() - 1);
        assertThat(testLateFee.getIsAutoLateFee()).isEqualTo(UPDATED_IS_AUTO_LATE_FEE);
        assertThat(testLateFee.getLateFeeDays()).isEqualTo(UPDATED_LATE_FEE_DAYS);
        assertThat(testLateFee.getChargeType()).isEqualTo(UPDATED_CHARGE_TYPE);
        assertThat(testLateFee.getFixedCharges()).isEqualTo(UPDATED_FIXED_CHARGES);
        assertThat(testLateFee.getPercentCharges()).isEqualTo(UPDATED_PERCENT_CHARGES);
        assertThat(testLateFee.getLateFeeFrequency()).isEqualTo(UPDATED_LATE_FEE_FREQUENCY);
        assertThat(testLateFee.getLateFeeRepeatDays()).isEqualTo(UPDATED_LATE_FEE_REPEAT_DAYS);
        assertThat(testLateFee.getAcademicYearId()).isEqualTo(UPDATED_ACADEMIC_YEAR_ID);
        assertThat(testLateFee.getTermId()).isEqualTo(UPDATED_TERM_ID);
        assertThat(testLateFee.getBranchId()).isEqualTo(UPDATED_BRANCH_ID);

        // Validate the LateFee in Elasticsearch
        verify(mockLateFeeSearchRepository, times(1)).save(testLateFee);
    }

    @Test
    @Transactional
    public void updateNonExistingLateFee() throws Exception {
        int databaseSizeBeforeUpdate = lateFeeRepository.findAll().size();

        // Create the LateFee
        LateFeeDTO lateFeeDTO = lateFeeMapper.toDto(lateFee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLateFeeMockMvc.perform(put("/api/late-fees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lateFeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LateFee in the database
        List<LateFee> lateFeeList = lateFeeRepository.findAll();
        assertThat(lateFeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the LateFee in Elasticsearch
        verify(mockLateFeeSearchRepository, times(0)).save(lateFee);
    }

    @Test
    @Transactional
    public void deleteLateFee() throws Exception {
        // Initialize the database
        lateFeeRepository.saveAndFlush(lateFee);

        int databaseSizeBeforeDelete = lateFeeRepository.findAll().size();

        // Delete the lateFee
        restLateFeeMockMvc.perform(delete("/api/late-fees/{id}", lateFee.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LateFee> lateFeeList = lateFeeRepository.findAll();
        assertThat(lateFeeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the LateFee in Elasticsearch
        verify(mockLateFeeSearchRepository, times(1)).deleteById(lateFee.getId());
    }

    @Test
    @Transactional
    public void searchLateFee() throws Exception {
        // Initialize the database
        lateFeeRepository.saveAndFlush(lateFee);
//        when(mockLateFeeSearchRepository.search(queryStringQuery("id:" + lateFee.getId())))
//            .thenReturn(Collections.singletonList(lateFee));
        // Search the lateFee
        restLateFeeMockMvc.perform(get("/api/_search/late-fees?query=id:" + lateFee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lateFee.getId().intValue())))
            .andExpect(jsonPath("$.[*].isAutoLateFee").value(hasItem(DEFAULT_IS_AUTO_LATE_FEE)))
            .andExpect(jsonPath("$.[*].lateFeeDays").value(hasItem(DEFAULT_LATE_FEE_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].chargeType").value(hasItem(DEFAULT_CHARGE_TYPE)))
            .andExpect(jsonPath("$.[*].fixedCharges").value(hasItem(DEFAULT_FIXED_CHARGES.intValue())))
            .andExpect(jsonPath("$.[*].percentCharges").value(hasItem(DEFAULT_PERCENT_CHARGES)))
            .andExpect(jsonPath("$.[*].lateFeeFrequency").value(hasItem(DEFAULT_LATE_FEE_FREQUENCY)))
            .andExpect(jsonPath("$.[*].lateFeeRepeatDays").value(hasItem(DEFAULT_LATE_FEE_REPEAT_DAYS.intValue())))
            .andExpect(jsonPath("$.[*].academicYearId").value(hasItem(DEFAULT_ACADEMIC_YEAR_ID.intValue())))
            .andExpect(jsonPath("$.[*].termId").value(hasItem(DEFAULT_TERM_ID.intValue())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LateFee.class);
        LateFee lateFee1 = new LateFee();
        lateFee1.setId(1L);
        LateFee lateFee2 = new LateFee();
        lateFee2.setId(lateFee1.getId());
        assertThat(lateFee1).isEqualTo(lateFee2);
        lateFee2.setId(2L);
        assertThat(lateFee1).isNotEqualTo(lateFee2);
        lateFee1.setId(null);
        assertThat(lateFee1).isNotEqualTo(lateFee2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LateFeeDTO.class);
        LateFeeDTO lateFeeDTO1 = new LateFeeDTO();
        lateFeeDTO1.setId(1L);
        LateFeeDTO lateFeeDTO2 = new LateFeeDTO();
        assertThat(lateFeeDTO1).isNotEqualTo(lateFeeDTO2);
        lateFeeDTO2.setId(lateFeeDTO1.getId());
        assertThat(lateFeeDTO1).isEqualTo(lateFeeDTO2);
        lateFeeDTO2.setId(2L);
        assertThat(lateFeeDTO1).isNotEqualTo(lateFeeDTO2);
        lateFeeDTO1.setId(null);
        assertThat(lateFeeDTO1).isNotEqualTo(lateFeeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(lateFeeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(lateFeeMapper.fromId(null)).isNull();
    }
}
