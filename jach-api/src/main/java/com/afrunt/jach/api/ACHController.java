package com.afrunt.jach.api;

import com.afrunt.jach.ACH;
import com.afrunt.jach.document.ACHDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ach")
@Tag(name = "ACH", description = "ACH file processing operations")
public class ACHController {

    private final ACH ach;
    private final ObjectMapper objectMapper;

    public ACHController(ACH ach) {
        this.ach = ach;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Parse a NACHA ACH file", description = "Parses a NACHA-format ACH file and returns a structured JSON representation")
    public ResponseEntity<Map<String, Object>> parse(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            ACHDocument document = ach.read(is);
            Map<String, Object> result = documentToMap(document);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Generate a NACHA ACH file", description = "Generates a NACHA-format ACH file from a JSON representation")
    public ResponseEntity<String> generate(@RequestBody String json) {
        try {
            ACHDocument document = ach.read(extractNachaFromJson(json));
            String nachaOutput = ach.write(document);
            return ResponseEntity.ok(nachaOutput);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating ACH file: " + e.getMessage());
        }
    }

    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Validate a NACHA ACH file", description = "Validates a NACHA-format ACH file and returns validation results")
    public ResponseEntity<Map<String, Object>> validate(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, String>> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            ACHDocument document = ach.read(is);
            String roundTrip = ach.write(document);

            // Verify the file can be parsed and re-written successfully
            ach.read(new java.io.ByteArrayInputStream(roundTrip.getBytes(StandardCharsets.UTF_8)));

            result.put("valid", true);
            result.put("errors", errors);
        } catch (Exception e) {
            result.put("valid", false);
            Map<String, String> errorDetail = new LinkedHashMap<>();
            errorDetail.put("field", "file");
            errorDetail.put("message", e.getMessage());
            errors.add(errorDetail);
            result.put("errors", errors);
        }

        return ResponseEntity.ok(result);
    }

    @SuppressWarnings("unchecked")
    private String extractNachaFromJson(String json) {
        try {
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            if (map.containsKey("rawContent")) {
                return (String) map.get("rawContent");
            }
        } catch (Exception ignored) {
            // If JSON parsing fails, treat the input as raw NACHA content
        }
        return json;
    }

    private Map<String, Object> documentToMap(ACHDocument document) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fileHeader", beanToMap(document.getFileHeader()));
        result.put("fileControl", beanToMap(document.getFileControl()));
        result.put("batchCount", document.getBatches().size());

        List<Map<String, Object>> batches = new ArrayList<>();
        document.getBatches().forEach(batch -> {
            Map<String, Object> batchMap = new LinkedHashMap<>();
            batchMap.put("batchHeader", beanToMap(batch.getBatchHeader()));
            batchMap.put("batchControl", beanToMap(batch.getBatchControl()));
            batchMap.put("detailCount", batch.getDetails().size());

            List<Map<String, Object>> details = new ArrayList<>();
            batch.getDetails().forEach(detail -> {
                Map<String, Object> detailMap = new LinkedHashMap<>();
                detailMap.put("entryDetail", beanToMap(detail.getDetailRecord()));
                List<Object> addenda = new ArrayList<>();
                if (detail.getAddendaRecords() != null) {
                    detail.getAddendaRecords().forEach(a -> addenda.add(beanToMap(a)));
                }
                detailMap.put("addendaRecords", addenda);
                details.add(detailMap);
            });
            batchMap.put("details", details);
            batches.add(batchMap);
        });

        result.put("batches", batches);
        result.put("rawContent", ach.write(document));
        return result;
    }

    private Map<String, Object> beanToMap(Object bean) {
        if (bean == null) return null;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.convertValue(bean, Map.class);
            return map;
        } catch (Exception e) {
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("type", bean.getClass().getSimpleName());
            fallback.put("toString", bean.toString());
            return fallback;
        }
    }
}
