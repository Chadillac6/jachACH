package com.afrunt.jach.api.dto;

import com.afrunt.jach.document.ACHBatch;
import com.afrunt.jach.document.ACHBatchDetail;
import com.afrunt.jach.document.ACHDocument;
import com.afrunt.jach.domain.*;
import com.afrunt.jach.domain.addenda.GeneralAddendaRecord;
import com.afrunt.jach.domain.detail.*;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DtoMapper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyMMdd");

    public ACHDocumentDTO toDto(ACHDocument document) {
        ACHDocumentDTO dto = new ACHDocumentDTO();
        dto.setNumberOfLines(document.getNumberOfLines());

        if (document.getFileHeader() != null) {
            dto.setFileHeader(toFileHeaderDto(document.getFileHeader()));
        }
        if (document.getFileControl() != null) {
            dto.setFileControl(toFileControlDto(document.getFileControl()));
        }
        if (document.getBatches() != null) {
            dto.setBatches(document.getBatches().stream()
                    .map(this::toBatchDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private FileHeaderDTO toFileHeaderDto(FileHeader header) {
        FileHeaderDTO dto = new FileHeaderDTO();
        dto.setRecordTypeCode(header.getRecordTypeCode());
        dto.setPriorityCode(header.getPriorityCode());
        dto.setImmediateDestination(header.getImmediateDestination());
        dto.setImmediateOrigin(header.getImmediateOrigin());
        if (header.getFileCreationDate() != null) {
            synchronized (DATE_FORMAT) {
                dto.setFileCreationDate(DATE_FORMAT.format(header.getFileCreationDate()));
            }
        }
        dto.setFileCreationTime(header.getFileCreationTime());
        dto.setFileIdModifier(header.getFileIdModifier());
        dto.setRecordSize(header.getRecordSize());
        dto.setBlockingFactor(header.getBlockingFactor());
        dto.setFormatCode(header.getFormatCode());
        dto.setImmediateDestinationName(header.getImmediateDestinationName());
        dto.setImmediateOriginName(header.getImmediateOriginName());
        dto.setReferenceCode(header.getReferenceCode());
        return dto;
    }

    private FileControlDTO toFileControlDto(FileControl control) {
        FileControlDTO dto = new FileControlDTO();
        dto.setRecordTypeCode(control.getRecordTypeCode());
        dto.setBatchCount(control.getBatchCount());
        dto.setBlockCount(control.getBlockCount());
        dto.setEntryAddendaCount(control.getEntryAddendaCount());
        dto.setEntryHashTotals(control.getEntryHashTotals());
        dto.setTotalDebits(control.getTotalDebits());
        dto.setTotalCredits(control.getTotalCredits());
        return dto;
    }

    private ACHBatchDTO toBatchDto(ACHBatch batch) {
        ACHBatchDTO dto = new ACHBatchDTO();
        dto.setBatchType(batch.getBatchType());

        if (batch.getBatchHeader() != null) {
            dto.setBatchHeader(toBatchHeaderDto(batch.getBatchHeader()));
        }
        if (batch.getBatchControl() != null) {
            dto.setBatchControl(toBatchControlDto(batch.getBatchControl()));
        }
        if (batch.getDetails() != null) {
            dto.setDetails(batch.getDetails().stream()
                    .map(this::toBatchDetailDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private BatchHeaderDTO toBatchHeaderDto(BatchHeader header) {
        BatchHeaderDTO dto = new BatchHeaderDTO();
        dto.setRecordTypeCode(header.getRecordTypeCode());
        dto.setServiceClassCode(header.getServiceClassCode());
        dto.setStandardEntryClassCode(header.getStandardEntryClassCode());
        dto.setCompanyEntryDescription(header.getCompanyEntryDescription());
        if (header.getEffectiveEntryDate() != null) {
            synchronized (DATE_FORMAT) {
                dto.setEffectiveEntryDate(DATE_FORMAT.format(header.getEffectiveEntryDate()));
            }
        }
        dto.setSettlementDate(header.getSettlementDate());
        dto.setOriginatorStatusCode(header.getOriginatorStatusCode());
        dto.setOriginatorDFIIdentifier(header.getOriginatorDFIIdentifier());
        dto.setBatchNumber(header.getBatchNumber());

        if (header instanceof GeneralBatchHeader) {
            GeneralBatchHeader gbh = (GeneralBatchHeader) header;
            dto.setCompanyName(gbh.getCompanyName());
            dto.setCompanyDiscretionaryData(gbh.getCompanyDiscretionaryData());
            dto.setCompanyIdentification(gbh.getCompanyID());
            dto.setCompanyDescriptiveDate(gbh.getCompanyDescriptiveDate());
        }
        return dto;
    }

    private BatchControlDTO toBatchControlDto(BatchControl control) {
        BatchControlDTO dto = new BatchControlDTO();
        dto.setRecordTypeCode(control.getRecordTypeCode());
        dto.setServiceClassCode(control.getServiceClassCode());
        dto.setEntryAddendaCount(control.getEntryAddendaCount());
        dto.setEntryHash(control.getEntryHash());
        dto.setTotalDebits(control.getTotalDebits());
        dto.setTotalCredits(control.getTotalCredits());
        dto.setCompanyIdentification(control.getCompanyIdentification());
        dto.setMessageAuthenticationCode(control.getMessageAuthenticationCode());
        dto.setOriginatingDfiIdentification(control.getOriginatingDfiIdentification());
        dto.setBatchNumber(control.getBatchNumber());
        return dto;
    }

    private ACHBatchDetailDTO toBatchDetailDto(ACHBatchDetail detail) {
        ACHBatchDetailDTO dto = new ACHBatchDetailDTO();

        if (detail.getDetailRecord() != null) {
            EntryDetail entry = detail.getDetailRecord();
            Map<String, Object> detailMap = new LinkedHashMap<>();
            detailMap.put("recordTypeCode", entry.getRecordTypeCode());
            detailMap.put("transactionCode", entry.getTransactionCode());
            detailMap.put("receivingDfiIdentification", entry.getReceivingDfiIdentification());
            detailMap.put("checkDigit", entry.getCheckDigit());
            detailMap.put("amount", entry.getAmount());
            detailMap.put("addendaRecordIndicator", entry.getAddendaRecordIndicator());
            detailMap.put("traceNumber", entry.getTraceNumber());
            detailMap.put("type", entry.getClass().getSimpleName());

            if (entry instanceof NonIATEntryDetail) {
                NonIATEntryDetail nonIat = (NonIATEntryDetail) entry;
                detailMap.put("dfiAccountNumber", nonIat.getDfiAccountNumber());
            }

            extractAdditionalFields(entry, detailMap);
            dto.setDetailRecord(detailMap);
        }

        if (detail.getAddendaRecords() != null && !detail.getAddendaRecords().isEmpty()) {
            List<Map<String, Object>> addendaList = new ArrayList<>();
            for (AddendaRecord addenda : detail.getAddendaRecords()) {
                Map<String, Object> addendaMap = new LinkedHashMap<>();
                addendaMap.put("recordTypeCode", addenda.getRecordTypeCode());
                addendaMap.put("addendaTypeCode", addenda.getAddendaTypeCode());
                addendaMap.put("type", addenda.getClass().getSimpleName());
                if (addenda instanceof GeneralAddendaRecord) {
                    GeneralAddendaRecord gen = (GeneralAddendaRecord) addenda;
                    addendaMap.put("paymentRelatedInformation", gen.getPaymentRelatedInformation());
                    addendaMap.put("addendaSequenceNumber", gen.getAddendaSequenceNumber());
                    addendaMap.put("entryDetailSequenceNumber", gen.getEntryDetailSequenceNumber());
                }
                addendaList.add(addendaMap);
            }
            dto.setAddendaRecords(addendaList);
        }

        return dto;
    }

    private void extractAdditionalFields(EntryDetail entry, Map<String, Object> map) {
        for (Method method : entry.getClass().getMethods()) {
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterCount() == 0
                    && !name.equals("getClass") && !name.equals("getRecord")
                    && !map.containsKey(toCamelCase(name))) {
                try {
                    Object value = method.invoke(entry);
                    if (value != null) {
                        map.put(toCamelCase(name), value);
                    }
                } catch (Exception ignored) {
                    // skip fields that can't be read
                }
            }
        }
    }

    private String toCamelCase(String getterName) {
        String field = getterName.substring(3);
        return Character.toLowerCase(field.charAt(0)) + field.substring(1);
    }
}
