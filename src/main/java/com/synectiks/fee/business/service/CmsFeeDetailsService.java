package com.synectiks.fee.business.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.synectiks.fee.constant.CmsConstants;
import com.synectiks.fee.domain.FeeCategory;
import com.synectiks.fee.domain.FeeDetails;
import com.synectiks.fee.domain.vo.CmsFeeDetails;
import com.synectiks.fee.repository.FeeDetailsRepository;
import com.synectiks.fee.service.util.CommonUtil;
import com.synectiks.fee.service.util.DateFormatUtil;

@Component
public class CmsFeeDetailsService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private FeeDetailsRepository feeDetailsRepository;
    
    @Autowired
    private CmsFeeCategoryService cmsFeeCategoryService;
    
    public List<FeeDetails> getFeeDetailsListOnFilterCriteria(Map<String, String> criteriaMap){
    	FeeDetails fd = new FeeDetails();
    	boolean isFilter = false;
    	if(criteriaMap.get("id") != null) {
    		fd.setId(Long.parseLong(criteriaMap.get("id")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("feeParticularsName") != null) {
    		fd.setFeeParticularsName(criteriaMap.get("feeParticularsName"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("feeParticularDesc") != null) {
    		fd.setFeeParticularDesc(criteriaMap.get("feeParticularDesc"));
    		isFilter = true;
    	}
    	
    	if(criteriaMap.get("studentType") != null) {
    		fd.setStudentType(criteriaMap.get("studentType"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("gender") != null) {
    		fd.setGender(criteriaMap.get("gender"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("amount") != null) {
    		fd.setAmount(Float.parseFloat(criteriaMap.get("amount")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("status") != null) {
    		fd.setStatus(criteriaMap.get("status"));
    		isFilter = true;
    	}
    	
    	if(criteriaMap.get("createdBy") != null) {
    		fd.setCreatedBy(criteriaMap.get("createdBy"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("createdOn") != null) {
    		fd.setCreatedOn(DateFormatUtil.convertStringToLocalDate(criteriaMap.get("createdOn"), CmsConstants.DATE_FORMAT_dd_MM_yyyy));
    		isFilter = true;
    	}
    	if(criteriaMap.get("updatedBy") != null) {
    		fd.setUpdatedBy(criteriaMap.get("updatedBy"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("updatedOn") != null) {
    		fd.setUpdatedOn(DateFormatUtil.convertStringToLocalDate(criteriaMap.get("updatedOn"), CmsConstants.DATE_FORMAT_dd_MM_yyyy));
    		isFilter = true;
    	}
    	
    	if(criteriaMap.get("startDate") != null) {
    		fd.setStartDate(DateFormatUtil.convertStringToLocalDate(criteriaMap.get("startDate"), CmsConstants.DATE_FORMAT_dd_MM_yyyy));
    		isFilter = true;
    	}
    	
    	if(criteriaMap.get("endDate") != null) {
    		fd.setEndDate(DateFormatUtil.convertStringToLocalDate(criteriaMap.get("endDate"), CmsConstants.DATE_FORMAT_dd_MM_yyyy ));
    		isFilter = true;
    	}
    	
    	if(criteriaMap.get("branchId") != null) {
    		fd.setBranchId(Long.parseLong(criteriaMap.get("branchId")));
    		isFilter = true;
    	}
    	
    	if(criteriaMap.get("departmentId") != null) {
    		fd.setDepartmentId(Long.parseLong(criteriaMap.get("departmentId")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("batchId") != null) {
    		fd.setBatchId(Long.parseLong(criteriaMap.get("batchId")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("facilityId") != null) {
    		fd.setFacilityId(Long.parseLong(criteriaMap.get("facilityId")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("facilityId") != null) {
    		fd.setFacilityId(Long.parseLong(criteriaMap.get("facilityId")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("transportRouteId") != null) {
    		fd.setTransportRouteId(Long.parseLong(criteriaMap.get("transportRouteId")));
    		isFilter = true;
    	}
    	
    	if(criteriaMap.get("feeCategoryId") != null) {
    		FeeCategory fc = this.cmsFeeCategoryService.getFeeCategory(Long.parseLong(criteriaMap.get("feeCategoryId")));
    		if(fc != null) {
    			fd.setFeeCategory(fc);
        		isFilter = true;
    		}
    	}
    	
    	List<FeeDetails> list = null;
    	if(isFilter) {
    		logger.debug("Filter criteria object : ",fd);
    		list = this.feeDetailsRepository.findAll(Example.of(fd), Sort.by(Direction.DESC, "id"));
    	}else {
    		logger.debug("No filter criteria given");
    		list = this.feeDetailsRepository.findAll(Sort.by(Direction.DESC, "id"));
    	}
        
    	Collections.sort(list, (o1, o2) -> o2.getId().compareTo(o1.getId()));
    	return list;
    }
    
    public List<CmsFeeDetails> getCmsFeeDetailsListOnFilterCriteria(Map<String, String> criteriaMap){
    	List<FeeDetails> fdList = getFeeDetailsListOnFilterCriteria(criteriaMap);
    	List <CmsFeeDetails> list = new ArrayList<>();
    	for(FeeDetails fd: fdList) {
    		CmsFeeDetails cfd = convertFeeDetailsToCmsFeeDetails(fd);
    		list.add(cfd);
    	}
    	Collections.sort(list, (o1, o2) -> o2.getId().compareTo(o1.getId()));
    	return list;
    }
    	
    public List<FeeDetails> getFeeDetailsList(Long branchId) {
    	List<FeeDetails> list = null;
    	if(branchId != null) {
    		FeeDetails dd = new FeeDetails();
    		dd.setBranchId(branchId);
    		list = this.feeDetailsRepository.findAll(Example.of(dd));
    		logger.debug("FeeDetails list found for the given branch id. "+branchId+". List : ",list);
    	}else {
    		list = this.feeDetailsRepository.findAll();
    	}
        Collections.sort(list, (o1, o2) -> o2.getId().compareTo(o1.getId()));
        return list;
    }
    
    public List<CmsFeeDetails> getCmsFeeDetailsList(Long branchId) {
    	List<FeeDetails> fdList = getFeeDetailsList(branchId);
    	List <CmsFeeDetails> list = new ArrayList<>();
    	for(FeeDetails fd: fdList) {
    		CmsFeeDetails cfd = convertFeeDetailsToCmsFeeDetails(fd);
    		list.add(cfd);
    	}
    	Collections.sort(list, (o1, o2) -> o2.getId().compareTo(o1.getId()));
        return list;
    }
    
    public FeeDetails getFeeDetails(Long id){
    	Optional<FeeDetails> br = this.feeDetailsRepository.findById(id);
    	if(br.isPresent()) {
    		logger.debug("FeeDetails object found for the given id. "+id);
    		return br.get();
    	}
    	logger.debug("FeeDetails object not found for the given id. "+id+". Returning null");
        return null;
    }
    
    public CmsFeeDetails getCmsFeeDetails(Long id){
    	FeeDetails fd = getFeeDetails(id);
    	if(fd != null) {
    		CmsFeeDetails cfd = convertFeeDetailsToCmsFeeDetails(fd);
    		logger.debug("CmsFeeDetails object found for the given id. "+id);
    		return cfd;
    	}
    	logger.debug("CmsFeeDetails object not found for the given id. "+id+". Returning null");
        return null;
    }
    
    private CmsFeeDetails convertFeeDetailsToCmsFeeDetails(FeeDetails fd) {
    	if(fd != null) {
    		CmsFeeDetails cfd = CommonUtil.createCopyProperties(fd, CmsFeeDetails.class);
    		if(fd.getCreatedOn() != null) {
    			cfd.setStrCreatedOn(DateFormatUtil.changeLocalDateFormat(fd.getCreatedOn(), CmsConstants.DATE_FORMAT_dd_MM_yyyy));
    			cfd.setCreatedOn(null);
    		}
    		if(fd.getUpdatedOn() != null) {
    			cfd.setStrUpdatedOn(DateFormatUtil.changeLocalDateFormat(fd.getUpdatedOn(), CmsConstants.DATE_FORMAT_dd_MM_yyyy));
    			cfd.setUpdatedOn(null);
    		}
    		if(fd.getStartDate() != null) {
    			cfd.setStrStartDate(DateFormatUtil.changeLocalDateFormat(fd.getStartDate(), CmsConstants.DATE_FORMAT_dd_MM_yyyy));
    			cfd.setStartDate(null);
    		}
    		if(fd.getEndDate() != null) {
    			cfd.setStrEndDate(DateFormatUtil.changeLocalDateFormat(fd.getEndDate(), CmsConstants.DATE_FORMAT_dd_MM_yyyy));
    			cfd.setEndDate(null);
    		}
    		return cfd;
    	}
    	return null;
    }
    
}
