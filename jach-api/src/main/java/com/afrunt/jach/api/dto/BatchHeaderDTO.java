package com.afrunt.jach.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchHeaderDTO {
    private String recordTypeCode;
    private String serviceClassCode;
    private String companyName;
    private String companyDiscretionaryData;
    private String companyIdentification;
    private String standardEntryClassCode;
    private String companyEntryDescription;
    private String companyDescriptiveDate;
    private String effectiveEntryDate;
    private Short settlementDate;
    private String originatorStatusCode;
    private String originatorDFIIdentifier;
    private Integer batchNumber;

    public String getRecordTypeCode() { return recordTypeCode; }
    public void setRecordTypeCode(String recordTypeCode) { this.recordTypeCode = recordTypeCode; }

    public String getServiceClassCode() { return serviceClassCode; }
    public void setServiceClassCode(String serviceClassCode) { this.serviceClassCode = serviceClassCode; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyDiscretionaryData() { return companyDiscretionaryData; }
    public void setCompanyDiscretionaryData(String companyDiscretionaryData) { this.companyDiscretionaryData = companyDiscretionaryData; }

    public String getCompanyIdentification() { return companyIdentification; }
    public void setCompanyIdentification(String companyIdentification) { this.companyIdentification = companyIdentification; }

    public String getStandardEntryClassCode() { return standardEntryClassCode; }
    public void setStandardEntryClassCode(String standardEntryClassCode) { this.standardEntryClassCode = standardEntryClassCode; }

    public String getCompanyEntryDescription() { return companyEntryDescription; }
    public void setCompanyEntryDescription(String companyEntryDescription) { this.companyEntryDescription = companyEntryDescription; }

    public String getCompanyDescriptiveDate() { return companyDescriptiveDate; }
    public void setCompanyDescriptiveDate(String companyDescriptiveDate) { this.companyDescriptiveDate = companyDescriptiveDate; }

    public String getEffectiveEntryDate() { return effectiveEntryDate; }
    public void setEffectiveEntryDate(String effectiveEntryDate) { this.effectiveEntryDate = effectiveEntryDate; }

    public Short getSettlementDate() { return settlementDate; }
    public void setSettlementDate(Short settlementDate) { this.settlementDate = settlementDate; }

    public String getOriginatorStatusCode() { return originatorStatusCode; }
    public void setOriginatorStatusCode(String originatorStatusCode) { this.originatorStatusCode = originatorStatusCode; }

    public String getOriginatorDFIIdentifier() { return originatorDFIIdentifier; }
    public void setOriginatorDFIIdentifier(String originatorDFIIdentifier) { this.originatorDFIIdentifier = originatorDFIIdentifier; }

    public Integer getBatchNumber() { return batchNumber; }
    public void setBatchNumber(Integer batchNumber) { this.batchNumber = batchNumber; }
}
