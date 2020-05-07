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
import com.synectiks.fee.domain.Invoice;
import com.synectiks.fee.repository.InvoiceRepository;
import com.synectiks.fee.repository.search.InvoiceSearchRepository;
import com.synectiks.fee.service.InvoiceService;
import com.synectiks.fee.service.dto.InvoiceDTO;
import com.synectiks.fee.service.mapper.InvoiceMapper;
import com.synectiks.fee.web.rest.errors.ExceptionTranslator;

/**
 * Integration tests for the {@Link InvoiceResource} REST controller.
 */
@SpringBootTest(classes = FeeApp.class)
public class InvoiceResourceIT {

    private static final String DEFAULT_INVOICE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NUMBER = "BBBBBBBBBB";

    private static final Long DEFAULT_AMOUNT_PAID = 1L;
    private static final Long UPDATED_AMOUNT_PAID = 2L;

    private static final LocalDate DEFAULT_PAYMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PAYMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_NEXT_PAYMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_NEXT_PAYMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_OUT_STANDING_AMOUNT = 1L;
    private static final Long UPDATED_OUT_STANDING_AMOUNT = 2L;

    private static final String DEFAULT_MODE_OF_PAYMENT = "AAAAAAAAAA";
    private static final String UPDATED_MODE_OF_PAYMENT = "BBBBBBBBBB";

    private static final Long DEFAULT_CHEQUE_NUMBER = 1L;
    private static final Long UPDATED_CHEQUE_NUMBER = 2L;

    private static final Long DEFAULT_DEMAND_DRAFT_NUMBER = 1L;
    private static final Long UPDATED_DEMAND_DRAFT_NUMBER = 2L;

    private static final String DEFAULT_ONLINE_TXN_REF_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ONLINE_TXN_REF_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_BANK = "AAAAAAAAAA";
    private static final String UPDATED_BANK = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_UPDATED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATED_ON = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_ACADEMIC_YEAR_ID = 1L;
    private static final Long UPDATED_ACADEMIC_YEAR_ID = 2L;

    private static final Long DEFAULT_BRANCH_ID = 1L;
    private static final Long UPDATED_BRANCH_ID = 2L;

    private static final Long DEFAULT_DEPARTMENT_ID = 1L;
    private static final Long UPDATED_DEPARTMENT_ID = 2L;

    private static final Long DEFAULT_STUDENT_ID = 1L;
    private static final Long UPDATED_STUDENT_ID = 2L;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private InvoiceService invoiceService;

    /**
     * This repository is mocked in the com.synectiks.fee.repository.search test package.
     *
     * @see com.synectiks.fee.repository.search.InvoiceSearchRepositoryMockConfiguration
     */
    @Autowired
    private InvoiceSearchRepository mockInvoiceSearchRepository;

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

    private MockMvc restInvoiceMockMvc;

    private Invoice invoice;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final InvoiceResource invoiceResource = new InvoiceResource(invoiceService);
        this.restInvoiceMockMvc = MockMvcBuilders.standaloneSetup(invoiceResource)
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
    public static Invoice createEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .invoiceNumber(DEFAULT_INVOICE_NUMBER)
            .amountPaid(DEFAULT_AMOUNT_PAID)
            .paymentDate(DEFAULT_PAYMENT_DATE)
            .nextPaymentDate(DEFAULT_NEXT_PAYMENT_DATE)
            .outStandingAmount(DEFAULT_OUT_STANDING_AMOUNT)
            .modeOfPayment(DEFAULT_MODE_OF_PAYMENT)
            .chequeNumber(DEFAULT_CHEQUE_NUMBER)
            .demandDraftNumber(DEFAULT_DEMAND_DRAFT_NUMBER)
            .onlineTxnRefNumber(DEFAULT_ONLINE_TXN_REF_NUMBER)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .comments(DEFAULT_COMMENTS)
            .updatedBy(DEFAULT_UPDATED_BY)
            .bank(DEFAULT_BANK)
            .updatedOn(DEFAULT_UPDATED_ON)
            .academicYearId(DEFAULT_ACADEMIC_YEAR_ID)
            .branchId(DEFAULT_BRANCH_ID)
            .departmentId(DEFAULT_DEPARTMENT_ID)
            .studentId(DEFAULT_STUDENT_ID);
        return invoice;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invoice createUpdatedEntity(EntityManager em) {
        Invoice invoice = new Invoice()
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .nextPaymentDate(UPDATED_NEXT_PAYMENT_DATE)
            .outStandingAmount(UPDATED_OUT_STANDING_AMOUNT)
            .modeOfPayment(UPDATED_MODE_OF_PAYMENT)
            .chequeNumber(UPDATED_CHEQUE_NUMBER)
            .demandDraftNumber(UPDATED_DEMAND_DRAFT_NUMBER)
            .onlineTxnRefNumber(UPDATED_ONLINE_TXN_REF_NUMBER)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .comments(UPDATED_COMMENTS)
            .updatedBy(UPDATED_UPDATED_BY)
            .bank(UPDATED_BANK)
            .updatedOn(UPDATED_UPDATED_ON)
            .academicYearId(UPDATED_ACADEMIC_YEAR_ID)
            .branchId(UPDATED_BRANCH_ID)
            .departmentId(UPDATED_DEPARTMENT_ID)
            .studentId(UPDATED_STUDENT_ID);
        return invoice;
    }

    @BeforeEach
    public void initTest() {
        invoice = createEntity(em);
    }

    @Test
    @Transactional
    public void createInvoice() throws Exception {
        int databaseSizeBeforeCreate = invoiceRepository.findAll().size();

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);
        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isCreated());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate + 1);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(DEFAULT_INVOICE_NUMBER);
        assertThat(testInvoice.getAmountPaid()).isEqualTo(DEFAULT_AMOUNT_PAID);
        assertThat(testInvoice.getPaymentDate()).isEqualTo(DEFAULT_PAYMENT_DATE);
        assertThat(testInvoice.getNextPaymentDate()).isEqualTo(DEFAULT_NEXT_PAYMENT_DATE);
        assertThat(testInvoice.getOutStandingAmount()).isEqualTo(DEFAULT_OUT_STANDING_AMOUNT);
        assertThat(testInvoice.getModeOfPayment()).isEqualTo(DEFAULT_MODE_OF_PAYMENT);
        assertThat(testInvoice.getChequeNumber()).isEqualTo(DEFAULT_CHEQUE_NUMBER);
        assertThat(testInvoice.getDemandDraftNumber()).isEqualTo(DEFAULT_DEMAND_DRAFT_NUMBER);
        assertThat(testInvoice.getOnlineTxnRefNumber()).isEqualTo(DEFAULT_ONLINE_TXN_REF_NUMBER);
        assertThat(testInvoice.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testInvoice.getComments()).isEqualTo(DEFAULT_COMMENTS);
        assertThat(testInvoice.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
        assertThat(testInvoice.getBank()).isEqualTo(DEFAULT_BANK);
        assertThat(testInvoice.getUpdatedOn()).isEqualTo(DEFAULT_UPDATED_ON);
        assertThat(testInvoice.getAcademicYearId()).isEqualTo(DEFAULT_ACADEMIC_YEAR_ID);
        assertThat(testInvoice.getBranchId()).isEqualTo(DEFAULT_BRANCH_ID);
        assertThat(testInvoice.getDepartmentId()).isEqualTo(DEFAULT_DEPARTMENT_ID);
        assertThat(testInvoice.getStudentId()).isEqualTo(DEFAULT_STUDENT_ID);

        // Validate the Invoice in Elasticsearch
        verify(mockInvoiceSearchRepository, times(1)).save(testInvoice);
    }

    @Test
    @Transactional
    public void createInvoiceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = invoiceRepository.findAll().size();

        // Create the Invoice with an existing ID
        invoice.setId(1L);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInvoiceMockMvc.perform(post("/api/invoices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeCreate);

        // Validate the Invoice in Elasticsearch
        verify(mockInvoiceSearchRepository, times(0)).save(invoice);
    }


    @Test
    @Transactional
    public void getAllInvoices() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get all the invoiceList
        restInvoiceMockMvc.perform(get("/api/invoices?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().intValue())))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(DEFAULT_AMOUNT_PAID.intValue())))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].nextPaymentDate").value(hasItem(DEFAULT_NEXT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].outStandingAmount").value(hasItem(DEFAULT_OUT_STANDING_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].modeOfPayment").value(hasItem(DEFAULT_MODE_OF_PAYMENT.toString())))
            .andExpect(jsonPath("$.[*].chequeNumber").value(hasItem(DEFAULT_CHEQUE_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].demandDraftNumber").value(hasItem(DEFAULT_DEMAND_DRAFT_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].onlineTxnRefNumber").value(hasItem(DEFAULT_ONLINE_TXN_REF_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY.toString())))
            .andExpect(jsonPath("$.[*].bank").value(hasItem(DEFAULT_BANK.toString())))
            .andExpect(jsonPath("$.[*].updatedOn").value(hasItem(DEFAULT_UPDATED_ON.toString())))
            .andExpect(jsonPath("$.[*].academicYearId").value(hasItem(DEFAULT_ACADEMIC_YEAR_ID.intValue())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())))
            .andExpect(jsonPath("$.[*].departmentId").value(hasItem(DEFAULT_DEPARTMENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].studentId").value(hasItem(DEFAULT_STUDENT_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        // Get the invoice
        restInvoiceMockMvc.perform(get("/api/invoices/{id}", invoice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(invoice.getId().intValue()))
            .andExpect(jsonPath("$.invoiceNumber").value(DEFAULT_INVOICE_NUMBER.toString()))
            .andExpect(jsonPath("$.amountPaid").value(DEFAULT_AMOUNT_PAID.intValue()))
            .andExpect(jsonPath("$.paymentDate").value(DEFAULT_PAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.nextPaymentDate").value(DEFAULT_NEXT_PAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.outStandingAmount").value(DEFAULT_OUT_STANDING_AMOUNT.intValue()))
            .andExpect(jsonPath("$.modeOfPayment").value(DEFAULT_MODE_OF_PAYMENT.toString()))
            .andExpect(jsonPath("$.chequeNumber").value(DEFAULT_CHEQUE_NUMBER.intValue()))
            .andExpect(jsonPath("$.demandDraftNumber").value(DEFAULT_DEMAND_DRAFT_NUMBER.intValue()))
            .andExpect(jsonPath("$.onlineTxnRefNumber").value(DEFAULT_ONLINE_TXN_REF_NUMBER.toString()))
            .andExpect(jsonPath("$.paymentStatus").value(DEFAULT_PAYMENT_STATUS.toString()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY.toString()))
            .andExpect(jsonPath("$.bank").value(DEFAULT_BANK.toString()))
            .andExpect(jsonPath("$.updatedOn").value(DEFAULT_UPDATED_ON.toString()))
            .andExpect(jsonPath("$.academicYearId").value(DEFAULT_ACADEMIC_YEAR_ID.intValue()))
            .andExpect(jsonPath("$.branchId").value(DEFAULT_BRANCH_ID.intValue()))
            .andExpect(jsonPath("$.departmentId").value(DEFAULT_DEPARTMENT_ID.intValue()))
            .andExpect(jsonPath("$.studentId").value(DEFAULT_STUDENT_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingInvoice() throws Exception {
        // Get the invoice
        restInvoiceMockMvc.perform(get("/api/invoices/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();

        // Update the invoice
        Invoice updatedInvoice = invoiceRepository.findById(invoice.getId()).get();
        // Disconnect from session so that the updates on updatedInvoice are not directly saved in db
        em.detach(updatedInvoice);
        updatedInvoice
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .amountPaid(UPDATED_AMOUNT_PAID)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .nextPaymentDate(UPDATED_NEXT_PAYMENT_DATE)
            .outStandingAmount(UPDATED_OUT_STANDING_AMOUNT)
            .modeOfPayment(UPDATED_MODE_OF_PAYMENT)
            .chequeNumber(UPDATED_CHEQUE_NUMBER)
            .demandDraftNumber(UPDATED_DEMAND_DRAFT_NUMBER)
            .onlineTxnRefNumber(UPDATED_ONLINE_TXN_REF_NUMBER)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .comments(UPDATED_COMMENTS)
            .updatedBy(UPDATED_UPDATED_BY)
            .bank(UPDATED_BANK)
            .updatedOn(UPDATED_UPDATED_ON)
            .academicYearId(UPDATED_ACADEMIC_YEAR_ID)
            .branchId(UPDATED_BRANCH_ID)
            .departmentId(UPDATED_DEPARTMENT_ID)
            .studentId(UPDATED_STUDENT_ID);
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(updatedInvoice);

        restInvoiceMockMvc.perform(put("/api/invoices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isOk());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);
        Invoice testInvoice = invoiceList.get(invoiceList.size() - 1);
        assertThat(testInvoice.getInvoiceNumber()).isEqualTo(UPDATED_INVOICE_NUMBER);
        assertThat(testInvoice.getAmountPaid()).isEqualTo(UPDATED_AMOUNT_PAID);
        assertThat(testInvoice.getPaymentDate()).isEqualTo(UPDATED_PAYMENT_DATE);
        assertThat(testInvoice.getNextPaymentDate()).isEqualTo(UPDATED_NEXT_PAYMENT_DATE);
        assertThat(testInvoice.getOutStandingAmount()).isEqualTo(UPDATED_OUT_STANDING_AMOUNT);
        assertThat(testInvoice.getModeOfPayment()).isEqualTo(UPDATED_MODE_OF_PAYMENT);
        assertThat(testInvoice.getChequeNumber()).isEqualTo(UPDATED_CHEQUE_NUMBER);
        assertThat(testInvoice.getDemandDraftNumber()).isEqualTo(UPDATED_DEMAND_DRAFT_NUMBER);
        assertThat(testInvoice.getOnlineTxnRefNumber()).isEqualTo(UPDATED_ONLINE_TXN_REF_NUMBER);
        assertThat(testInvoice.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testInvoice.getComments()).isEqualTo(UPDATED_COMMENTS);
        assertThat(testInvoice.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
        assertThat(testInvoice.getBank()).isEqualTo(UPDATED_BANK);
        assertThat(testInvoice.getUpdatedOn()).isEqualTo(UPDATED_UPDATED_ON);
        assertThat(testInvoice.getAcademicYearId()).isEqualTo(UPDATED_ACADEMIC_YEAR_ID);
        assertThat(testInvoice.getBranchId()).isEqualTo(UPDATED_BRANCH_ID);
        assertThat(testInvoice.getDepartmentId()).isEqualTo(UPDATED_DEPARTMENT_ID);
        assertThat(testInvoice.getStudentId()).isEqualTo(UPDATED_STUDENT_ID);

        // Validate the Invoice in Elasticsearch
        verify(mockInvoiceSearchRepository, times(1)).save(testInvoice);
    }

    @Test
    @Transactional
    public void updateNonExistingInvoice() throws Exception {
        int databaseSizeBeforeUpdate = invoiceRepository.findAll().size();

        // Create the Invoice
        InvoiceDTO invoiceDTO = invoiceMapper.toDto(invoice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInvoiceMockMvc.perform(put("/api/invoices")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(invoiceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Invoice in the database
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Invoice in Elasticsearch
        verify(mockInvoiceSearchRepository, times(0)).save(invoice);
    }

    @Test
    @Transactional
    public void deleteInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);

        int databaseSizeBeforeDelete = invoiceRepository.findAll().size();

        // Delete the invoice
        restInvoiceMockMvc.perform(delete("/api/invoices/{id}", invoice.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Invoice> invoiceList = invoiceRepository.findAll();
        assertThat(invoiceList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Invoice in Elasticsearch
        verify(mockInvoiceSearchRepository, times(1)).deleteById(invoice.getId());
    }

    @Test
    @Transactional
    public void searchInvoice() throws Exception {
        // Initialize the database
        invoiceRepository.saveAndFlush(invoice);
//        when(mockInvoiceSearchRepository.search(queryStringQuery("id:" + invoice.getId())))
//            .thenReturn(Collections.singletonList(invoice));
        // Search the invoice
        restInvoiceMockMvc.perform(get("/api/_search/invoices?query=id:" + invoice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invoice.getId().intValue())))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER)))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(DEFAULT_AMOUNT_PAID.intValue())))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].nextPaymentDate").value(hasItem(DEFAULT_NEXT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].outStandingAmount").value(hasItem(DEFAULT_OUT_STANDING_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].modeOfPayment").value(hasItem(DEFAULT_MODE_OF_PAYMENT)))
            .andExpect(jsonPath("$.[*].chequeNumber").value(hasItem(DEFAULT_CHEQUE_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].demandDraftNumber").value(hasItem(DEFAULT_DEMAND_DRAFT_NUMBER.intValue())))
            .andExpect(jsonPath("$.[*].onlineTxnRefNumber").value(hasItem(DEFAULT_ONLINE_TXN_REF_NUMBER)))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS)))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)))
            .andExpect(jsonPath("$.[*].bank").value(hasItem(DEFAULT_BANK)))
            .andExpect(jsonPath("$.[*].updatedOn").value(hasItem(DEFAULT_UPDATED_ON.toString())))
            .andExpect(jsonPath("$.[*].academicYearId").value(hasItem(DEFAULT_ACADEMIC_YEAR_ID.intValue())))
            .andExpect(jsonPath("$.[*].branchId").value(hasItem(DEFAULT_BRANCH_ID.intValue())))
            .andExpect(jsonPath("$.[*].departmentId").value(hasItem(DEFAULT_DEPARTMENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].studentId").value(hasItem(DEFAULT_STUDENT_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Invoice.class);
        Invoice invoice1 = new Invoice();
        invoice1.setId(1L);
        Invoice invoice2 = new Invoice();
        invoice2.setId(invoice1.getId());
        assertThat(invoice1).isEqualTo(invoice2);
        invoice2.setId(2L);
        assertThat(invoice1).isNotEqualTo(invoice2);
        invoice1.setId(null);
        assertThat(invoice1).isNotEqualTo(invoice2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InvoiceDTO.class);
        InvoiceDTO invoiceDTO1 = new InvoiceDTO();
        invoiceDTO1.setId(1L);
        InvoiceDTO invoiceDTO2 = new InvoiceDTO();
        assertThat(invoiceDTO1).isNotEqualTo(invoiceDTO2);
        invoiceDTO2.setId(invoiceDTO1.getId());
        assertThat(invoiceDTO1).isEqualTo(invoiceDTO2);
        invoiceDTO2.setId(2L);
        assertThat(invoiceDTO1).isNotEqualTo(invoiceDTO2);
        invoiceDTO1.setId(null);
        assertThat(invoiceDTO1).isNotEqualTo(invoiceDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(invoiceMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(invoiceMapper.fromId(null)).isNull();
    }
}
