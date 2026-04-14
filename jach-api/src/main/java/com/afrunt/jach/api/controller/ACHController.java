package com.afrunt.jach.api.controller;

import com.afrunt.jach.ACH;
import com.afrunt.jach.api.dto.ACHDocumentDTO;
import com.afrunt.jach.api.dto.DtoMapper;
import com.afrunt.jach.api.dto.ValidationResultDTO;
import com.afrunt.jach.api.dto.ValidationResultDTO.ValidationError;
import com.afrunt.jach.document.ACHDocument;
import com.afrunt.jach.exception.ACHException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ach")
@Tag(name = "ACH", description = "ACH file parsing, generation, and validation")
public class ACHController {

    private final DtoMapper dtoMapper = new DtoMapper();

    private ACH createACH() {
        return new ACH();
    }

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Parse an ACH file", description = "Accepts an ACH file upload and returns a structured JSON representation of the document")
    @ApiResponse(responseCode = "200", description = "Successfully parsed ACH file",
            content = @Content(schema = @Schema(implementation = ACHDocumentDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid ACH file")
    public ResponseEntity<ACHDocumentDTO> parse(@RequestParam("file") MultipartFile file) throws Exception {
        ACH ach = createACH();
        ACHDocument document = ach.read(file.getInputStream());
        ACHDocumentDTO dto = dtoMapper.toDto(document);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Generate an ACH file", description = "Accepts a JSON ACH document and generates a NACHA flat file")
    @ApiResponse(responseCode = "200", description = "Successfully generated ACH file content",
            content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "400", description = "Invalid ACH document data")
    public ResponseEntity<String> generate(@RequestBody String achJson) throws Exception {
        ACH ach = createACH();
        ACHDocument document = ach.read(achJson);
        String output = ach.write(document);
        return ResponseEntity.ok(output);
    }

    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Validate an ACH file", description = "Validates an ACH file and returns validation results")
    @ApiResponse(responseCode = "200", description = "Validation completed",
            content = @Content(schema = @Schema(implementation = ValidationResultDTO.class)))
    public ResponseEntity<ValidationResultDTO> validate(@RequestParam("file") MultipartFile file) throws Exception {
        ValidationResultDTO result = new ValidationResultDTO();

        String content;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            content = reader.lines().collect(Collectors.joining("\n"));
        }
        String[] lines = content.split("\n");

        // Check record lengths
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            // Skip empty trailing lines (block padding lines of all 9s are valid)
            if (line.trim().isEmpty()) {
                continue;
            }
            if (line.length() != 94) {
                result.addError(new ValidationError(i + 1, "recordLength",
                        "Record length is " + line.length() + ", expected 94 characters"));
            }
        }

        // Attempt to parse the file
        try {
            ACH ach = createACH();
            ACHDocument document = ach.read(content);

            // Validate required fields
            if (document.getFileHeader() == null) {
                result.addError(new ValidationError(0, "fileHeader", "File header is missing"));
            }
            if (document.getFileControl() == null) {
                result.addError(new ValidationError(0, "fileControl", "File control is missing"));
            }

            // Validate batch totals
            if (document.getFileControl() != null && document.getBatches() != null) {
                int expectedBatchCount = document.getBatches().size();
                Integer actualBatchCount = document.getFileControl().getBatchCount();
                if (actualBatchCount != null && actualBatchCount != expectedBatchCount) {
                    result.addError(new ValidationError(0, "batchCount",
                            "File control batch count (" + actualBatchCount +
                                    ") does not match actual number of batches (" + expectedBatchCount + ")"));
                }
            }

        } catch (ACHException e) {
            result.addError(new ValidationError(0, "parse", e.getMessage()));
        }

        result.setValid(result.getErrors().isEmpty());
        return ResponseEntity.ok(result);
    }
}
