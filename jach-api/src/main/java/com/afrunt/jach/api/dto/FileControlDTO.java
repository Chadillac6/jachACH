package com.afrunt.jach.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileControlDTO {
    private String recordTypeCode;
    private Integer batchCount;
    private Integer blockCount;
    private Integer entryAddendaCount;
    private Long entryHashTotals;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;

    public String getRecordTypeCode() { return recordTypeCode; }
    public void setRecordTypeCode(String recordTypeCode) { this.recordTypeCode = recordTypeCode; }

    public Integer getBatchCount() { return batchCount; }
    public void setBatchCount(Integer batchCount) { this.batchCount = batchCount; }

    public Integer getBlockCount() { return blockCount; }
    public void setBlockCount(Integer blockCount) { this.blockCount = blockCount; }

    public Integer getEntryAddendaCount() { return entryAddendaCount; }
    public void setEntryAddendaCount(Integer entryAddendaCount) { this.entryAddendaCount = entryAddendaCount; }

    public Long getEntryHashTotals() { return entryHashTotals; }
    public void setEntryHashTotals(Long entryHashTotals) { this.entryHashTotals = entryHashTotals; }

    public BigDecimal getTotalDebits() { return totalDebits; }
    public void setTotalDebits(BigDecimal totalDebits) { this.totalDebits = totalDebits; }

    public BigDecimal getTotalCredits() { return totalCredits; }
    public void setTotalCredits(BigDecimal totalCredits) { this.totalCredits = totalCredits; }
}
