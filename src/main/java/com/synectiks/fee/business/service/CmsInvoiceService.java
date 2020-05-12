package com.synectiks.fee.business.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synectiks.fee.constant.CmsConstants;
import com.synectiks.fee.domain.AcademicYear;
import com.synectiks.fee.domain.Branch;
import com.synectiks.fee.domain.Invoice;
import com.synectiks.fee.domain.Student;
import com.synectiks.fee.domain.vo.CmsInvoice;
import com.synectiks.fee.repository.InvoiceRepository;
import com.synectiks.fee.service.util.CommonUtil;
import com.synectiks.fee.service.util.DateFormatUtil;

@Service
@Transactional
public class CmsInvoiceService {

	private final Logger logger = LoggerFactory.getLogger(CmsInvoiceService.class);

	@Autowired
	private InvoiceRepository invoiceRepository;

	@PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CommonService commonService;
    

	public Long getTotalInvoice(Long branchId, Long academicYearId) {
    	Long a = getTotalPaidInvoice(branchId, academicYearId);
    	Long b = getTotalUnPaidInvoice(branchId, academicYearId);
    	Long c = getTotalCanceledInvoice(branchId, academicYearId);
    	return a+b+c;
    }

    public Long getTotalPaidInvoice(Long branchId, Long academicYearId) {
    	Invoice inv = new Invoice();

//    	if(branchId != null) {
    	    //    		Branch branch = new Branch();
           //    		branch.setId(branchId);
//            Branch branch = this.commonService.getBranchById(inv.getBranchId());
        
//        }

//    	AcademicYear ac = new AcademicYear();
//    	ac.setId(academicYearId);
    	inv.setBranchId(branchId);
    	inv.setPaymentStatus("PAID");
    	inv.setAcademicYearId(academicYearId);
//        AcademicYear ac = this.commonService.getAcademicYearById(inv.getAcademicYearId());
    	Example<Invoice> example = Example.of(inv);
    	Long cnt = this.invoiceRepository.count(example);
    	return cnt;
    }

    public Long getTotalUnPaidInvoice(Long branchId, Long academicYearId) {
    	Invoice inv = new Invoice();

//    	if(branchId != null) {
//    		Branch branch = new Branch();
//    		branch.setId(branchId);
//            Branch branch = this.commonService.getBranchById(inv.getBranchId());
    		
//    	}

//    	AcademicYear ac = new AcademicYear();
//    	ac.setId(academicYearId);
    	
    	inv.setBranchId(branchId);
    	inv.setPaymentStatus("UNPAID");
//    	AcademicYear ac = this.commonService.getAcademicYearById(inv.getAcademicYearId());
    	inv.setAcademicYearId(academicYearId);
    	Example<Invoice> example = Example.of(inv);
    	Long cnt = this.invoiceRepository.count(example);
    	return cnt;
    }

    public Long getTotalCanceledInvoice(Long branchId, Long academicYearId) {
    	Invoice inv = new Invoice();

//    	if(branchId != null) {
//    		Branch branch = new Branch();
//    		branch.setId(branchId);
//            Branch branch = this.commonService.getBranchById(inv.getBranchId());
            
//    	}
//    	AcademicYear ac = new AcademicYear();
//    	ac.setId(academicYearId);

    	inv.setBranchId(branchId);
    	inv.setPaymentStatus("CANCELED");
//        AcademicYear ac = this.commonService.getAcademicYearById(inv.getAcademicYearId());
        inv.setAcademicYearId(academicYearId);
    	Example<Invoice> example = Example.of(inv);
    	Long cnt = this.invoiceRepository.count(example);
    	return cnt;
    }



    public List<CmsInvoice> searchInvoice(String invoiceNumber, Long studentId, Long collegeId, Long branchId, Long academicYearId) throws Exception {
    	Invoice inv = new Invoice();
//    	College college = new College();
//    	college.setId(collegeId);
//    	Branch branch = new Branch();
//    	branch.setId(branchId);
//    	AcademicYear ay  = new AcademicYear();
//    	ay.setId(academicYearId);

        Branch branch = this.commonService.getBranchById(inv.getBranchId());
        inv.setBranchId(branch.getId());
        AcademicYear ac = this.commonService.getAcademicYearById(inv.getAcademicYearId());
        inv.setAcademicYearId(ac.getId());

    	if(!CommonUtil.isNullOrEmpty(invoiceNumber)) {
    		inv.setInvoiceNumber(invoiceNumber);
    	}
    	if(studentId != null && studentId >= 0) {
    		Student student = new Student();
    		student.setId(studentId);
    		inv.setStudentId(student.getId());
    	}
    	Example<Invoice> example = Example.of(inv);
    	List<Invoice> list = this.invoiceRepository.findAll(example);
    	List<CmsInvoice> ls = new ArrayList<>();
    	for(Invoice temp: list) {
//            String stDt = DateFormatUtil.changeDateFormat(CmsConstants.DATE_FORMAT_dd_MM_yyyy, temp.getPaymentDate());
            CmsInvoice ctm = CommonUtil.createCopyProperties(temp, CmsInvoice.class);
            ctm.setStrPaymentDate(DateFormatUtil.changeDateFormat(CmsConstants.DATE_FORMAT_dd_MM_yyyy, CmsConstants.DATE_FORMAT_yyyy_MM_dd, DateFormatUtil.changeDateFormat(CmsConstants.DATE_FORMAT_yyyy_MM_dd, DateFormatUtil.converUtilDateFromLocaDate(temp.getPaymentDate()))));
            ls.add(ctm);
        }
    	return ls;
    }

    public List<CmsInvoice> searchInvoiceOnType(String invoiceType, Long branchId, Long academicYearId) throws Exception {
    	Invoice inv = new Invoice();
//    	College college = new College();
//    	college.setId(collegeId);
//    	Branch branch = new Branch();
//    	branch.setId(branchId);
//    	AcademicYear ay  = new AcademicYear();
//    	ay.setId(academicYearId);

//        College college = this.commonService.getCollegeById(inv.getCollegeId());
//        inv.setCollegeId(collegeId);
//        Branch branch = this.commonService.getBranchById(inv.getBranchId());
        inv.setBranchId(branchId);
//        AcademicYear ac = this.commonService.getAcademicYearById(inv.getAcademicYearId());
        inv.setAcademicYearId(academicYearId);

    	if(!invoiceType.equalsIgnoreCase("TOTAL")) {
    		if(invoiceType.equalsIgnoreCase("PAID")) {
    			inv.setPaymentStatus("PAID");
    		}else if(invoiceType.equalsIgnoreCase("UNPAID")) {
    			inv.setPaymentStatus("UNPAID");
    		}else if(invoiceType.equalsIgnoreCase("CANCELED")) {
    			inv.setPaymentStatus("CANCELED");
    		}
    	}

    	Example<Invoice> example = Example.of(inv);
    	List<Invoice> list = this.invoiceRepository.findAll(example);
    	List<CmsInvoice> ls = new ArrayList<>();
    	for(Invoice temp: list) {
//            String stDt = DateFormatUtil.changeDateFormat(CmsConstants.DATE_FORMAT_dd_MM_yyyy, temp.getPaymentDate());
            CmsInvoice ctm = CommonUtil.createCopyProperties(temp, CmsInvoice.class);
            ctm.setStrPaymentDate(DateFormatUtil.changeDateFormat(CmsConstants.DATE_FORMAT_dd_MM_yyyy, CmsConstants.DATE_FORMAT_yyyy_MM_dd, DateFormatUtil.changeDateFormat(CmsConstants.DATE_FORMAT_yyyy_MM_dd, DateFormatUtil.converUtilDateFromLocaDate(temp.getPaymentDate()))));
            ls.add(ctm);
        }
    	return ls;
    }


    public CmsInvoice getInvoiceByStudentId(Student student) {
//    	Student student = new Student();
//    	student.setStudentPrimaryEmailId(studentMailId);
//    	Optional<Student> ost = this.studentRepository.findOne(Example.of(student));
    	CmsInvoice cmsInv = null;
    	if(student != null) {
    		Invoice inv = new Invoice();
    		inv.setStudentId(student.getId());
    		List<Invoice> ls = this.invoiceRepository.findAll(Example.of(inv), Sort.by(Direction.DESC, "id"));
    		if(ls != null && ls.size() > 0) {
    			cmsInv = CommonUtil.createCopyProperties(ls.get(0), CmsInvoice.class);
    			cmsInv.setStrNextPaymentDate(DateFormatUtil.changeLocalDateFormat(ls.get(0).getNextPaymentDate(), CmsConstants.DATE_FORMAT_dd_MM_yyyy));
    		}else {
    			cmsInv = new CmsInvoice();
    		}
    	}
    	return cmsInv;
    }

    public Long getTotalCollectedAmount(Branch branch, AcademicYear academicYear, LocalDate dt) {
    	LocalDate lastPaymentDate = null;
    	if( dt == null) {
    		@SuppressWarnings("unchecked")
    		Object dtResult = this.entityManager.createQuery("select max(inv.paymentDate) from Invoice inv where inv.academicYearId = :ay and inv.branchId = :br ")
        			.setParameter("ay", academicYear.getId())
        			.setParameter("br", branch.getId())
        			.getSingleResult();
        	lastPaymentDate = (LocalDate)dtResult;
        	logger.debug("Last payment date: "+lastPaymentDate);
    	}

    	Object result = this.entityManager.createQuery("select sum(inv.amountPaid) from Invoice inv where inv.paymentDate = :pmtDate and inv.academicYearId = :ay and inv.branchId = :br ")
    			.setParameter("pmtDate", (dt != null ? dt : lastPaymentDate))
    			.setParameter("ay", academicYear.getId())
    			.setParameter("br", branch.getId())
    			.getSingleResult();
    	Long totalAmtCollected = (Long)result;
    	logger.debug("Total amount collected : "+totalAmtCollected);

    	return totalAmtCollected ;
    }

    public Long getTotalPendingAmount(Branch branch, AcademicYear academicYear, LocalDate dt) {
    	LocalDate lastPaymentDate = null;
    	if( dt == null) {
    		@SuppressWarnings("unchecked")
    		Object dtResult = this.entityManager.createQuery("select max(inv.paymentDate) from Invoice inv where inv.academicYearId = :ay and inv.branchId = :br ")
        			.setParameter("ay", academicYear.getId())
        			.setParameter("br", branch.getId())
        			.getSingleResult();
        	lastPaymentDate = (LocalDate)dtResult;
        	logger.debug("Last payment date: "+lastPaymentDate);
    	}

    	Object result = this.entityManager.createQuery("select sum(inv.outStandingAmount) from Invoice inv where inv.paymentDate = :pmtDate and inv.academicYearId = :ay and inv.branchId = :br ")
    			.setParameter("pmtDate", (dt != null ? dt : lastPaymentDate))
    			.setParameter("ay", academicYear.getId())
    			.setParameter("br", branch.getId())
    			.getSingleResult();
    	Long totalAmtPending = (Long)result;
    	logger.debug("Total pending amount: "+totalAmtPending);

    	return totalAmtPending ;
    }

    public Long getTotalOverDueAmount(Branch branch, AcademicYear academicYear) {
    	Object result = this.entityManager.createQuery("select sum(inv.outStandingAmount) from Invoice inv where inv.nextPaymentDate < :pmtDate and inv.academicYearId = :ay and inv.branchId = :br ")
    			.setParameter("pmtDate", LocalDate.now())
    			.setParameter("ay", academicYear.getId())
    			.setParameter("br", branch.getId())
    			.getSingleResult();
    	Long totalAmtOverDue = (Long)result;
    	logger.debug("Total over due amount: "+totalAmtOverDue);

    	return totalAmtOverDue ;
    }

}
