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
import com.synectiks.fee.domain.FeeCategory;
import com.synectiks.fee.repository.FeeCategoryRepository;
import com.synectiks.fee.repository.search.FeeCategorySearchRepository;
import com.synectiks.fee.service.FeeCategoryService;
import com.synectiks.fee.service.dto.FeeCategoryDTO;
import com.synectiks.fee.service.mapper.FeeCategoryMapper;
import com.synectiks.fee.web.rest.errors.ExceptionTranslator;

/**
 * Integration tests for the {@Link FeeCategoryResource} REST controller.
 */
@SpringBootTest(classes = FeeApp.class)
public class FeeCategoryResourceIT {

    private static final String DEFAULT_CATEGORY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

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

    @Autowired
    private FeeCategoryRepository feeCategoryRepository;

    @Autowired
    private FeeCategoryMapper feeCategoryMapper;

    @Autowired
    private FeeCategoryService feeCategoryService;

    /**
     * This repository is mocked in the com.synectiks.fee.repository.search test package.
     *
     * @see com.synectiks.fee.repository.search.FeeCategorySearchRepositoryMockConfiguration
     */
    @Autowired
    private FeeCategorySearchRepository mockFeeCategorySearchRepository;

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

    private MockMvc restFeeCategoryMockMvc;

    private FeeCategory feeCategory;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FeeCategoryResource feeCategoryResource = new FeeCategoryResource(feeCategoryService);
        this.restFeeCategoryMockMvc = MockMvcBuilders.standaloneSetup(feeCategoryResource)
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
    public static FeeCategory createEntity(EntityManager em) {
        FeeCategory feeCategory = new FeeCategory()
            .categoryName(DEFAULT_CATEGORY_NAME)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdOn(DEFAULT_CREATED_ON)
            .updatedBy(DEFAULT_UPDATED_BY)
            .updatedOn(DEFAULT_UPDATED_ON)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .branchId(DEFAULT_BRANCH_ID);
        return feeCategory;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FeeCategory createUpdatedEntity(EntityManager em) {
        FeeCategory feeCategory = new FeeCategory()
            .categoryName(UPDATED_CATEGORY_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedOn(UPDATED_UPDATED_ON)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .branchId(UPDATED_BRANCH_ID);
        return feeCategory;
    }

    @BeforeEach
    public void initTest() {
        feeCategory = createEntity(em);
    }

    @Test
    @Transactional
    public void createFeeCategory() throws Exception {
        int databaseSizeBeforeCreate = feeCategoryRepository.findAll().size();

        // Create the FeeCategory
        FeeCategoryDTO feeCategoryDTO = feeCategoryMapper.toDto(feeCategory);
        restFeeCategoryMockMvc.perform(post("/api/fee-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feeCategoryDTO)))
            .andExpect(status().isCreated());

        // Validate the FeeCategory in the database
        List<FeeCategory> feeCategoryList = feeCategoryRepository.findAll();
        assertThat(feeCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        FeeCategory testFeeCategory = feeCategoryList.get(feeCategoryList.size() - 1);
        assertThat(testFeeCategory.getCategoryName()).isEqualTo(DEFAULT_CATEGORY_NAME);
        assertThat(testFeeCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFeeCategory.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testFeeCategory.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testFeeCategory.getCreatedOn()).isEqualTo(DEFAULT_CREATED_ON);
        assertThat(testFeeCategory.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testFeeCategory.getUpdatedOn()).isEqualTo(DEFAULT_UPDATED_ON);
        assertThat(testFeeCategory.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testFeeCategory.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testFeeCategory.getBranchId()).isEqualTo(DEFAULT_BRANCH_ID);

        // Validate the FeeCategory in Elasticsearch
        verify(mockFeeCategorySearchRepository, times(1)).save(testFeeCategory);
    }

    @Test
    @Transactional
    public void createFeeCategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = feeCategoryRepository.findAll().size();

        // Create the FeeCategory with an existing ID
        feeCategory.setId(1L);
        FeeCategoryDTO feeCategoryDTO = feeCategoryMapper.toDto(feeCategory);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeeCategoryMockMvc.perform(post("/api/fee-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feeCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FeeCategory in the database
        List<FeeCategory> feeCategoryList = feeCategoryRepository.findAll();
        assertThat(feeCategoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the FeeCategory in Elasticsearch
        verify(mockFeeCategorySearchRepository, times(0)).save(feeCategory);
    }


    @Test
    @Transactional
    public void getAllFeeCategories() throws Exception {
        // Initialize the database
        feeCategoryRepository.saveAndFlush(feeCategory);

        // Get all the feeCategoryList
        restFeeCategoryMockMvc.perform(get("/api/fee-categories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feeCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].categoryName").value(hasItem(DEFAULT_CATEGORY_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY.toString())))
            .andExpect(jsonPath("$.[*].updatedOn").value(hasItem(DEFAULT_UPDATED_ON.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getFeeCategory() throws Exception {
        // Initialize the database
        feeCategoryRepository.saveAndFlush(feeCategory);

        // Get the feeCategory
        restFeeCategoryMockMvc.perform(get("/api/fee-categories/{id}", feeCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(feeCategory.getId().intValue()))
            .andExpect(jsonPath("$.categoryName").value(DEFAULT_CATEGORY_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdOn").value(DEFAULT_CREATED_ON.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY.toString()))
            .andExpect(jsonPath("$.updatedOn").value(DEFAULT_UPDATED_ON.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.branchId").value(DEFAULT_BRANCH_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFeeCategory() throws Exception {
        // Get the feeCategory
        restFeeCategoryMockMvc.perform(get("/api/fee-categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeeCategory() throws Exception {
        // Initialize the database
        feeCategoryRepository.saveAndFlush(feeCategory);

        int databaseSizeBeforeUpdate = feeCategoryRepository.findAll().size();

        // Update the feeCategory
        FeeCategory updatedFeeCategory = feeCategoryRepository.findById(feeCategory.getId()).get();
        // Disconnect from session so that the updates on updatedFeeCategory are not directly saved in db
        em.detach(updatedFeeCategory);
        updatedFeeCategory
            .categoryName(UPDATED_CATEGORY_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdOn(UPDATED_CREATED_ON)
            .updatedBy(UPDATED_UPDATED_BY)
            .updatedOn(UPDATED_UPDATED_ON)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .branchId(UPDATED_BRANCH_ID);
        FeeCategoryDTO feeCategoryDTO = feeCategoryMapper.toDto(updatedFeeCategory);

        restFeeCategoryMockMvc.perform(put("/api/fee-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feeCategoryDTO)))
            .andExpect(status().isOk());

        // Validate the FeeCategory in the database
        List<FeeCategory> feeCategoryList = feeCategoryRepository.findAll();
        assertThat(feeCategoryList).hasSize(databaseSizeBeforeUpdate);
        FeeCategory testFeeCategory = feeCategoryList.get(feeCategoryList.size() - 1);
        assertThat(testFeeCategory.getCategoryName()).isEqualTo(UPDATED_CATEGORY_NAME);
        assertThat(testFeeCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFeeCategory.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testFeeCategory.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testFeeCategory.getCreatedOn()).isEqualTo(UPDATED_CREATED_ON);
        assertThat(testFeeCategory.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testFeeCategory.getUpdatedOn()).isEqualTo(UPDATED_UPDATED_ON);
        assertThat(testFeeCategory.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testFeeCategory.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testFeeCategory.getBranchId()).isEqualTo(UPDATED_BRANCH_ID);

        // Validate the FeeCategory in Elasticsearch
        verify(mockFeeCategorySearchRepository, times(1)).save(testFeeCategory);
    }

    @Test
    @Transactional
    public void updateNonExistingFeeCategory() throws Exception {
        int databaseSizeBeforeUpdate = feeCategoryRepository.findAll().size();

        // Create the FeeCategory
        FeeCategoryDTO feeCategoryDTO = feeCategoryMapper.toDto(feeCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeeCategoryMockMvc.perform(put("/api/fee-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(feeCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FeeCategory in the database
        List<FeeCategory> feeCategoryList = feeCategoryRepository.findAll();
        assertThat(feeCategoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FeeCategory in Elasticsearch
        verify(mockFeeCategorySearchRepository, times(0)).save(feeCategory);
    }

    @Test
    @Transactional
    public void deleteFeeCategory() throws Exception {
        // Initialize the database
        feeCategoryRepository.saveAndFlush(feeCategory);

        int databaseSizeBeforeDelete = feeCategoryRepository.findAll().size();

        // Delete the feeCategory
        restFeeCategoryMockMvc.perform(delete("/api/fee-categories/{id}", feeCategory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FeeCategory> feeCategoryList = feeCategoryRepository.findAll();
        assertThat(feeCategoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FeeCategory in Elasticsearch
        verify(mockFeeCategorySearchRepository, times(1)).deleteById(feeCategory.getId());
    }

    @Test
    @Transactional
    public void searchFeeCategory() throws Exception {
        // Initialize the database
        feeCategoryRepository.saveAndFlush(feeCategory);
//        when(mockFeeCategorySearchRepository.search(queryStringQuery("id:" + feeCategory.getId())))
//            .thenReturn(Collections.singletonList(feeCategory));
        // Search the feeCategory
        restFeeCategoryMockMvc.perform(get("/api/_search/fee-categories?query=id:" + feeCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feeCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].categoryName").value(hasItem(DEFAULT_CATEGORY_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdOn").value(hasItem(DEFAULT_CREATED_ON.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].updatedOn").value(hasItem(DEFAULT_UPDATED_ON.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeeCategory.class);
        FeeCategory feeCategory1 = new FeeCategory();
        feeCategory1.setId(1L);
        FeeCategory feeCategory2 = new FeeCategory();
        feeCategory2.setId(feeCategory1.getId());
        assertThat(feeCategory1).isEqualTo(feeCategory2);
        feeCategory2.setId(2L);
        assertThat(feeCategory1).isNotEqualTo(feeCategory2);
        feeCategory1.setId(null);
        assertThat(feeCategory1).isNotEqualTo(feeCategory2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FeeCategoryDTO.class);
        FeeCategoryDTO feeCategoryDTO1 = new FeeCategoryDTO();
        feeCategoryDTO1.setId(1L);
        FeeCategoryDTO feeCategoryDTO2 = new FeeCategoryDTO();
        assertThat(feeCategoryDTO1).isNotEqualTo(feeCategoryDTO2);
        feeCategoryDTO2.setId(feeCategoryDTO1.getId());
        assertThat(feeCategoryDTO1).isEqualTo(feeCategoryDTO2);
        feeCategoryDTO2.setId(2L);
        assertThat(feeCategoryDTO1).isNotEqualTo(feeCategoryDTO2);
        feeCategoryDTO1.setId(null);
        assertThat(feeCategoryDTO1).isNotEqualTo(feeCategoryDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(feeCategoryMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(feeCategoryMapper.fromId(null)).isNull();
    }
}
