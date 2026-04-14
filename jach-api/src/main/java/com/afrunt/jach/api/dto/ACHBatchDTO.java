package com.afrunt.jach.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ACHBatchDTO {
    private BatchHeaderDTO batchHeader;
    private BatchControlDTO batchControl;
    private List<ACHBatchDetailDTO> details;
    private String batchType;

    public BatchHeaderDTO getBatchHeader() { return batchHeader; }
    public void setBatchHeader(BatchHeaderDTO batchHeader) { this.batchHeader = batchHeader; }

    public BatchControlDTO getBatchControl() { return batchControl; }
    public void setBatchControl(BatchControlDTO batchControl) { this.batchControl = batchControl; }

    public List<ACHBatchDetailDTO> getDetails() { return details; }
    public void setDetails(List<ACHBatchDetailDTO> details) { this.details = details; }

    public String getBatchType() { return batchType; }
    public void setBatchType(String batchType) { this.batchType = batchType; }
}
