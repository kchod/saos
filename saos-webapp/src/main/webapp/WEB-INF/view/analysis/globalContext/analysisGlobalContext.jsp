<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/common/taglibs.jsp" %>


<div class="form-section-container" >
    <c:set var="courtCriteriaNestedPath" value="globalFilter.courtCriteria"/>
    <c:set var="courtCriteria" value="${analysisForm.globalFilter.courtCriteria}"/>


	<%@ include file="../../common/search/courtCriteriaInfoSection.jsp" %>
	
	<%@ include file="../../common/search/courtCriteriaFormSection.jsp" %>
	
	
	   
	<%@ include file="judgmentDateRangeInfoSection.jsp" %>
	
	<%@ include file="judgmentDateRangeFormSection.jsp" %>
    
    
    
</div>

