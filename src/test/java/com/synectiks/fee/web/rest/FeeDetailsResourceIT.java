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

import java.time.LocalDate;
import java.time.ZoneId;
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
import com.synectiks.fee.domain.FeeDetails;
import com.synectiks.fee.repository.FeeDetailsRepository;
import com.synectiks.fee.repository.search.FeeDetailsSearchRepository;
import com.synectiks.fee.service.FeeDetailsService;
import com.synectiks.fee.service.dto.FeeDetailsDTO;
import com.synectiks.fee.service.mapper.FeeDetailsMapper;
import com.synectiks.fee.web.rest.errors.ExceptionTranslator;

/**
 * Integration tests for the {@Link FeeDetailsResource} REST controller.
 */
@SpringBootTest(classes = FeeApp.class)
public class FeeDetailsResourceIT {

    private static final String DEFAULT_FEE_PARTICULARS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FEE_PARTICULARS_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FEE_PARTICULAR_DESC = "AAAAAAAAAA";
    private static final String UPDATED_FEE_PARTICULAR_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_STUDENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_GENDER = "AAAAAAAAAA";
    private static final String UPDATED_GENDER = "BBBBBBBBBB";

    private static final Float DEFAULT_AMOUNT = 1F;
    private static final Float UPDATED_AMOUNT = 2F;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_ON = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_UPDATED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATED_ON = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_BRANCH_ID = 1L;
    private static final Long UPDATED_BRANCH_ID = 2L;

    private static final Long DEFAULT_DEPARTMENT_ID = 1L;
    private static final Long UPDATED_DEPARTMENT_ID = 2L;

    private static final Long DEFAULT_BATCH_ID = 1L;
    private static final Long UPDATED_BATCH_ID = 2L;

    private static final Long DEFAULT_FACILITY_ID = 1L;
    private static final Long UPDATED_FACILITY_ID = 2L;

    private static final Long DEFAULT_TRANSPORT_ROUTE_ID = 1L;
    private static final Long UPDATED_TRANSPORT_ROUTE_ID = 2L;

    @Autowired
    private FeeDetailsRepository feeDetailsRepository;

    @Autowired
    private FeeDetailsMapper feeDetailsMapper;

    @Autowired
    private FeeDetailsService feeDetailsService;

    /**
     * This repository is mocked in the com.synectiks.fee.repository.search test package.
     *
     * @see com.synectiks.fee.repository.search.FeeDetailsSearchRepositoryMockConfiguration
     */
    @Autowired
    private FeeDetailsSearchRepository mockFeeDetailsSearchRepository;

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

    private MockMvc restFeeDetailsMockMvc;

    private FeeDetails feeDetails;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FeeDetailsResource feeDetailsResource = new FeeDetailsResource(feeDetailsService);
        this.restFeeDetailsMockMvc = MockMvcBuilders.standaloneSetup(feeDetailsResource)
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
    public static FeeDetails createEntity(EntityManager em) {
        FeeDetails feeDetails = new FeeDetails()
            .feeParticularsName(DEFAULT_FEE_PARTICULARS_NAME)
            .feeParticularDesc(DEFAULT_FEE_PARTICULAR_DESC)
            .studentType(DEFAULT_STUDENT_TYPE)
            .gender(DEFAULT_GENDER)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdOn(DEFAULT_CREATED_ON)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedOn(DEFAULT_UPDATED_ON)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .branchId(DEFAULT_BRANCH_ID)
            .departmentId(DEFAULT_DEPARTMENT_ID)
            .batchId(DEFAULT_BATCH_ID)
            .facilityId(DEFAULT_FACILITY_ID)
            .transportRouteId(DEFAULT_TRANSPORT_ROUTE_ID);
        return feeDetails;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeeDetails createUpdatedEntity(EntityManager em) {
        FeeDetails feeDetails = new FeeDetails()
            .feeParticularsName(UPDATED_FEE_PARTICULARS_NAME)
            .feeParticularDesc(UPDATED_FEE_PARTICULAR_DESC)
            .studentType(UPDATED_STUDENT_TYPE)
            .gender(UPDATED_GENDER)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedOn(UPDATED_UPDATED_ON)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .branchId(UPDATED_BRANCH_ID)
            .departmentId(UPDATED_DEPARTMENT_ID)
            .batchId(UPDATED_BATCH_ID)
            .facilityId(UPDATED_FACILITY_ID)
            .transportRouteId(UPDATED_TRANSPORT_ROUTE_ID);
        return feeDetails;
    }

    @BeforeEach
    public void initTest() {
        feeDetails = createEntity(em);
    }

    @Test
    @Transactional
    public void createFeeDetails() throws Exception {
        int databaseSizeBeforeCreate = feeDetailsRepository.findAll().size();

        // Create the FeeDetails
        FeeDetailsDTO feeDetailsDTO = feeDetailsMapper.toDto(feeDetails);
        restFeeDetailsMockMvc.perform(post("/api/fee-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feeDetailsDTO)))
            .andExpect(status().isCreated());

        // Validate the FeeDetails in the database
        List<FeeDetails> feeDetailsList = feeDetailsRepository.findAll();
        assertThat(feeDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        FeeDetails testFeeDetails = feeDetailsList.get(feeDetailsList.size() - 1);
        assertThat(testFeeDetails.getFeeParticularsName()).isEqualTo(DEFAULT_FEE_PARTICULARS_NAME);
        assertThat(testFeeDetails.getFeeParticularDesc()).isEqualTo(DEFAULT_FEE_PARTICULAR_DESC);
        assertThat(testFeeDetails.getStudentType()).isEqualTo(DEFAULT_STUDENT_TYPE);
        assertThat(testFeeDetails.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testFeeDetails.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testFeeDetails.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testFeeDetails.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testFeeDetails.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testFeeDetails.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testFeeDetails.getUpdatedOn()).isEqualTo(DEFAULT_UPDATED_ON);
        assertThat(testFeeDetails.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testFeeDetails.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testFeeDetails.getBranchId()).isEqualTo(DEFAULT_BRANCH_ID);
        assertThat(testFeeDetails.getDepartmentId()).isEqualTo(DEFAULT_DEPARTMENT_ID);
        assertThat(testFeeDetails.getBatchId()).isEqualTo(DEFAULT_BATCH_ID);
        assertThat(testFeeDetails.getFacilityId()).isEqualTo(DEFAULT_FACILITY_ID);
        assertThat(testFeeDetails.getTransportRouteId()).isEqualTo(DEFAULT_TRANSPORT_ROUTE_ID);

        // Validate the FeeDetails in Elasticsearch
        verify(mockFeeDetailsSearchRepository, times(1)).save(testFeeDetails);
    }

    @Test
    @Transactional
    public void createFeeDetailsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = feeDetailsRepository.findAll().size();

        // Create the FeeDetails with an existing ID
        feeDetails.setId(1L);
        FeeDetailsDTO feeDetailsDTO = feeDetailsMapper.toDto(feeDetails);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeeDetailsMockMvc.perform(post("/api/fee-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feeDetailsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FeeDetails in the database
        List<FeeDetails> feeDetailsList = feeDetailsRepository.findAll();
        assertThat(feeDetailsList).hasSize(databaseSizeBeforeCreate);

        // Validate the FeeDetails in Elasticsearch
        verify(mockFeeDetailsSearchRepository, times(0)).save(feeDetails);
    }


    @Test
    @Transactional
    public void getAllFeeDetails() throws Exception {
        // Initialize the database
        feeDetailsRepository.saveAndFlush(feeDetails);

        // Get all the feeDetailsList
        restFeeDetailsMockMvc.perform(get("/api/fee-details?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feeDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].feeParticularsName").value(hasItem(DEFAULT_FEE_PARTICULARS_NAME.toString())))
            .andExpect(jsonPath("$.[*].feeParticularDesc").value(hasItem(DEFAULT_FEE_PARTICULAR_DESC.toString())))
            .andExpect(jsonPath("$.[*].studentType").value(hasItem(DEFAULT_STUDENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY.toString())))
            .andExpect(jsonPath("$.[*].updatedOn").value(hasItem(DEFAULT_UPDATED_ON.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())))
            .andExpect(jsonPath("$.[*].departmentId").value(hasItem(DEFAULT_DEPARTMENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].batchId").value(hasItem(DEFAULT_BATCH_ID.intValue())))
            .andExpect(jsonPath("$.[*].facilityId").value(hasItem(DEFAULT_FACILITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].transportRouteId").value(hasItem(DEFAULT_TRANSPORT_ROUTE_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getFeeDetails() throws Exception {
        // Initialize the database
        feeDetailsRepository.saveAndFlush(feeDetails);

        // Get the feeDetails
        restFeeDetailsMockMvc.perform(get("/api/fee-details/{id}", feeDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(feeDetails.getId().intValue()))
            .andExpect(jsonPath("$.feeParticularsName").value(DEFAULT_FEE_PARTICULARS_NAME.toString()))
            .andExpect(jsonPath("$.feeParticularDesc").value(DEFAULT_FEE_PARTICULAR_DESC.toString()))
            .andExpect(jsonPath("$.studentType").value(DEFAULT_STUDENT_TYPE.toString()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdOn").value(DEFAULT_CREATED_ON.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY.toString()))
            .andExpect(jsonPath("$.updatedOn").value(DEFAULT_UPDATED_ON.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.branchId").value(DEFAULT_BRANCH_ID.intValue()))
            .andExpect(jsonPath("$.departmentId").value(DEFAULT_DEPARTMENT_ID.intValue()))
            .andExpect(jsonPath("$.batchId").value(DEFAULT_BATCH_ID.intValue()))
            .andExpect(jsonPath("$.facilityId").value(DEFAULT_FACILITY_ID.intValue()))
            .andExpect(jsonPath("$.transportRouteId").value(DEFAULT_TRANSPORT_ROUTE_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFeeDetails() throws Exception {
        // Get the feeDetails
        restFeeDetailsMockMvc.perform(get("/api/fee-details/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeeDetails() throws Exception {
        // Initialize the database
        feeDetailsRepository.saveAndFlush(feeDetails);

        int databaseSizeBeforeUpdate = feeDetailsRepository.findAll().size();

        // Update the feeDetails
        FeeDetails updatedFeeDetails = feeDetailsRepository.findById(feeDetails.getId()).get();
        // Disconnect from session so that the updates on updatedFeeDetails are not directly saved in db
        em.detach(updatedFeeDetails);
        updatedFeeDetails
            .feeParticularsName(UPDATED_FEE_PARTICULARS_NAME)
            .feeParticularDesc(UPDATED_FEE_PARTICULAR_DESC)
            .studentType(UPDATED_STUDENT_TYPE)
            .gender(UPDATED_GENDER)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedOn(UPDATED_UPDATED_ON)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .branchId(UPDATED_BRANCH_ID)
            .departmentId(UPDATED_DEPARTMENT_ID)
            .batchId(UPDATED_BATCH_ID)
            .facilityId(UPDATED_FACILITY_ID)
            .transportRouteId(UPDATED_TRANSPORT_ROUTE_ID);
        FeeDetailsDTO feeDetailsDTO = feeDetailsMapper.toDto(updatedFeeDetails);

        restFeeDetailsMockMvc.perform(put("/api/fee-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feeDetailsDTO)))
            .andExpect(status().isOk());

        // Validate the FeeDetails in the database
        List<FeeDetails> feeDetailsList = feeDetailsRepository.findAll();
        assertThat(feeDetailsList).hasSize(databaseSizeBeforeUpdate);
        FeeDetails testFeeDetails = feeDetailsList.get(feeDetailsList.size() - 1);
        assertThat(testFeeDetails.getFeeParticularsName()).isEqualTo(UPDATED_FEE_PARTICULARS_NAME);
        assertThat(testFeeDetails.getFeeParticularDesc()).isEqualTo(UPDATED_FEE_PARTICULAR_DESC);
        assertThat(testFeeDetails.getStudentType()).isEqualTo(UPDATED_STUDENT_TYPE);
        assertThat(testFeeDetails.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testFeeDetails.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testFeeDetails.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFeeDetails.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testFeeDetails.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testFeeDetails.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testFeeDetails.getUpdatedOn()).isEqualTo(UPDATED_UPDATED_ON);
        assertThat(testFeeDetails.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testFeeDetails.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testFeeDetails.getBranchId()).isEqualTo(UPDATED_BRANCH_ID);
        assertThat(testFeeDetails.getDepartmentId()).isEqualTo(UPDATED_DEPARTMENT_ID);
        assertThat(testFeeDetails.getBatchId()).isEqualTo(UPDATED_BATCH_ID);
        assertThat(testFeeDetails.getFacilityId()).isEqualTo(UPDATED_FACILITY_ID);
        assertThat(testFeeDetails.getTransportRouteId()).isEqualTo(UPDATED_TRANSPORT_ROUTE_ID);

        // Validate the FeeDetails in Elasticsearch
        verify(mockFeeDetailsSearchRepository, times(1)).save(testFeeDetails);
    }

    @Test
    @Transactional
    public void updateNonExistingFeeDetails() throws Exception {
        int databaseSizeBeforeUpdate = feeDetailsRepository.findAll().size();

        // Create the FeeDetails
        FeeDetailsDTO feeDetailsDTO = feeDetailsMapper.toDto(feeDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeeDetailsMockMvc.perform(put("/api/fee-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feeDetailsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FeeDetails in the database
        List<FeeDetails> feeDetailsList = feeDetailsRepository.findAll();
        assertThat(feeDetailsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FeeDetails in Elasticsearch
        verify(mockFeeDetailsSearchRepository, times(0)).save(feeDetails);
    }

    @Test
    @Transactional
    public void deleteFeeDetails() throws Exception {
        // Initialize the database
        feeDetailsRepository.saveAndFlush(feeDetails);

        int databaseSizeBeforeDelete = feeDetailsRepository.findAll().size();

        // Delete the feeDetails
        restFeeDetailsMockMvc.perform(delete("/api/fee-details/{id}", feeDetails.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FeeDetails> feeDetailsList = feeDetailsRepository.findAll();
        assertThat(feeDetailsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FeeDetails in Elasticsearch
        verify(mockFeeDetailsSearchRepository, times(1)).deleteById(feeDetails.getId());
    }

    @Test
    @Transactional
    public void searchFeeDetails() throws Exception {
        // Initialize the database
        feeDetailsRepository.saveAndFlush(feeDetails);
//        when(mockFeeDetailsSearchRepository.search(queryStringQuery("id:" + feeDetails.getId())))
//            .thenReturn(Collections.singletonList(feeDetails));
        // Search the feeDetails
        restFeeDetailsMockMvc.perform(get("/api/_search/fee-details?query=id:" + feeDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feeDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].feeParticularsName").value(hasItem(DEFAULT_FEE_PARTICULARS_NAME)))
            .andExpect(jsonPath("$.[*].feeParticularDesc").value(hasItem(DEFAULT_FEE_PARTICULAR_DESC)))
            .andExpect(jsonPath("$.[*].studentType").value(hasItem(DEFAULT_STUDENT_TYPE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].updatedOn").value(hasItem(DEFAULT_UPDATED_ON.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())))
            .andExpect(jsonPath("$.[*].departmentId").value(hasItem(DEFAULT_DEPARTMENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].batchId").value(hasItem(DEFAULT_BATCH_ID.intValue())))
            .andExpect(jsonPath("$.[*].facilityId").value(hasItem(DEFAULT_FACILITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].transportRouteId").value(hasItem(DEFAULT_TRANSPORT_ROUTE_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeeDetails.class);
        FeeDetails feeDetails1 = new FeeDetails();
        feeDetails1.setId(1L);
        FeeDetails feeDetails2 = new FeeDetails();
        feeDetails2.setId(feeDetails1.getId());
        assertThat(feeDetails1).isEqualTo(feeDetails2);
        feeDetails2.setId(2L);
        assertThat(feeDetails1).isNotEqualTo(feeDetails2);
        feeDetails1.setId(null);
        assertThat(feeDetails1).isNotEqualTo(feeDetails2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeeDetailsDTO.class);
        FeeDetailsDTO feeDetailsDTO1 = new FeeDetailsDTO();
        feeDetailsDTO1.setId(1L);
        FeeDetailsDTO feeDetailsDTO2 = new FeeDetailsDTO();
        assertThat(feeDetailsDTO1).isNotEqualTo(feeDetailsDTO2);
        feeDetailsDTO2.setId(feeDetailsDTO1.getId());
        assertThat(feeDetailsDTO1).isEqualTo(feeDetailsDTO2);
        feeDetailsDTO2.setId(2L);
        assertThat(feeDetailsDTO1).isNotEqualTo(feeDetailsDTO2);
        feeDetailsDTO1.setId(null);
        assertThat(feeDetailsDTO1).isNotEqualTo(feeDetailsDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(feeDetailsMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(feeDetailsMapper.fromId(null)).isNull();
    }
}
