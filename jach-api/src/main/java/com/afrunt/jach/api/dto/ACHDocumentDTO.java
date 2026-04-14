package com.afrunt.jach.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ACHDocumentDTO {
    private FileHeaderDTO fileHeader;
    private FileControlDTO fileControl;
    private List<ACHBatchDTO> batches;
    private int numberOfLines;

    public FileHeaderDTO getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(FileHeaderDTO fileHeader) {
        this.fileHeader = fileHeader;
    }

    public FileControlDTO getFileControl() {
        return fileControl;
    }

    public void setFileControl(FileControlDTO fileControl) {
        this.fileControl = fileControl;
    }

    public List<ACHBatchDTO> getBatches() {
        return batches;
    }

    public void setBatches(List<ACHBatchDTO> batches) {
        this.batches = batches;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
    }
}
