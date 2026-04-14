package com.afrunt.jach.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ACHBatchDetailDTO {
    private Map<String, Object> detailRecord;
    private List<Map<String, Object>> addendaRecords;

    public Map<String, Object> getDetailRecord() { return detailRecord; }
    public void setDetailRecord(Map<String, Object> detailRecord) { this.detailRecord = detailRecord; }

    public List<Map<String, Object>> getAddendaRecords() { return addendaRecords; }
    public void setAddendaRecords(List<Map<String, Object>> addendaRecords) { this.addendaRecords = addendaRecords; }
}
