package com.afrunt.jach.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileHeaderDTO {
    private String recordTypeCode;
    private String priorityCode;
    private String immediateDestination;
    private String immediateOrigin;
    private String fileCreationDate;
    private String fileCreationTime;
    private String fileIdModifier;
    private String recordSize;
    private String blockingFactor;
    private String formatCode;
    private String immediateDestinationName;
    private String immediateOriginName;
    private String referenceCode;

    public String getRecordTypeCode() { return recordTypeCode; }
    public void setRecordTypeCode(String recordTypeCode) { this.recordTypeCode = recordTypeCode; }

    public String getPriorityCode() { return priorityCode; }
    public void setPriorityCode(String priorityCode) { this.priorityCode = priorityCode; }

    public String getImmediateDestination() { return immediateDestination; }
    public void setImmediateDestination(String immediateDestination) { this.immediateDestination = immediateDestination; }

    public String getImmediateOrigin() { return immediateOrigin; }
    public void setImmediateOrigin(String immediateOrigin) { this.immediateOrigin = immediateOrigin; }

    public String getFileCreationDate() { return fileCreationDate; }
    public void setFileCreationDate(String fileCreationDate) { this.fileCreationDate = fileCreationDate; }

    public String getFileCreationTime() { return fileCreationTime; }
    public void setFileCreationTime(String fileCreationTime) { this.fileCreationTime = fileCreationTime; }

    public String getFileIdModifier() { return fileIdModifier; }
    public void setFileIdModifier(String fileIdModifier) { this.fileIdModifier = fileIdModifier; }

    public String getRecordSize() { return recordSize; }
    public void setRecordSize(String recordSize) { this.recordSize = recordSize; }

    public String getBlockingFactor() { return blockingFactor; }
    public void setBlockingFactor(String blockingFactor) { this.blockingFactor = blockingFactor; }

    public String getFormatCode() { return formatCode; }
    public void setFormatCode(String formatCode) { this.formatCode = formatCode; }

    public String getImmediateDestinationName() { return immediateDestinationName; }
    public void setImmediateDestinationName(String immediateDestinationName) { this.immediateDestinationName = immediateDestinationName; }

    public String getImmediateOriginName() { return immediateOriginName; }
    public void setImmediateOriginName(String immediateOriginName) { this.immediateOriginName = immediateOriginName; }

    public String getReferenceCode() { return referenceCode; }
    public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
}
