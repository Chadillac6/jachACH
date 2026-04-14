package com.afrunt.jach.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchControlDTO {
    private String recordTypeCode;
    private Integer serviceClassCode;
    private Integer entryAddendaCount;
    private BigInteger entryHash;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private String companyIdentification;
    private String messageAuthenticationCode;
    private String originatingDfiIdentification;
    private Integer batchNumber;

    public String getRecordTypeCode() { return recordTypeCode; }
    public void setRecordTypeCode(String recordTypeCode) { this.recordTypeCode = recordTypeCode; }

    public Integer getServiceClassCode() { return serviceClassCode; }
    public void setServiceClassCode(Integer serviceClassCode) { this.serviceClassCode = serviceClassCode; }

    public Integer getEntryAddendaCount() { return entryAddendaCount; }
    public void setEntryAddendaCount(Integer entryAddendaCount) { this.entryAddendaCount = entryAddendaCount; }

    public BigInteger getEntryHash() { return entryHash; }
    public void setEntryHash(BigInteger entryHash) { this.entryHash = entryHash; }

    public BigDecimal getTotalDebits() { return totalDebits; }
    public void setTotalDebits(BigDecimal totalDebits) { this.totalDebits = totalDebits; }

    public BigDecimal getTotalCredits() { return totalCredits; }
    public void setTotalCredits(BigDecimal totalCredits) { this.totalCredits = totalCredits; }

    public String getCompanyIdentification() { return companyIdentification; }
    public void setCompanyIdentification(String companyIdentification) { this.companyIdentification = companyIdentification; }

    public String getMessageAuthenticationCode() { return messageAuthenticationCode; }
    public void setMessageAuthenticationCode(String messageAuthenticationCode) { this.messageAuthenticationCode = messageAuthenticationCode; }

    public String getOriginatingDfiIdentification() { return originatingDfiIdentification; }
    public void setOriginatingDfiIdentification(String originatingDfiIdentification) { this.originatingDfiIdentification = originatingDfiIdentification; }

    public Integer getBatchNumber() { return batchNumber; }
    public void setBatchNumber(Integer batchNumber) { this.batchNumber = batchNumber; }
}
