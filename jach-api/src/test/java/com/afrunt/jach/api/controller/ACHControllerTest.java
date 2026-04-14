package com.afrunt.jach.api.controller;

import com.afrunt.jach.api.JachApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = JachApiApplication.class)
@AutoConfigureMockMvc
class ACHControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testParseEndpoint() throws Exception {
        byte[] achContent = Files.readAllBytes(new ClassPathResource("ach-payrol.txt").getFile().toPath());
        MockMultipartFile file = new MockMultipartFile("file", "ach-payrol.txt",
                MediaType.TEXT_PLAIN_VALUE, achContent);

        mockMvc.perform(multipart("/ach/parse").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fileHeader").exists())
                .andExpect(jsonPath("$.fileHeader.recordTypeCode").value("1"))
                .andExpect(jsonPath("$.fileHeader.immediateDestinationName").exists())
                .andExpect(jsonPath("$.fileControl").exists())
                .andExpect(jsonPath("$.batches").isArray())
                .andExpect(jsonPath("$.batches[0].batchHeader").exists())
                .andExpect(jsonPath("$.batches[0].batchHeader.standardEntryClassCode").value("PPD"))
                .andExpect(jsonPath("$.batches[0].details").isArray())
                .andExpect(jsonPath("$.batches[0].details[0].detailRecord.amount").exists());
    }

    @Test
    void testGenerateEndpoint() throws Exception {
        byte[] achContent = Files.readAllBytes(new ClassPathResource("ach-payrol.txt").getFile().toPath());
        String achString = new String(achContent);

        MvcResult result = mockMvc.perform(post("/ach/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(achString))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();

        String output = result.getResponse().getContentAsString();
        // Verify output is valid NACHA format: each line should be 94 chars
        String[] lines = output.split("\\r?\\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                assert line.length() == 94 : "Line length should be 94 but was " + line.length() + ": " + line;
            }
        }
    }

    @Test
    void testValidateValidFile() throws Exception {
        byte[] achContent = Files.readAllBytes(new ClassPathResource("ach-payrol.txt").getFile().toPath());
        MockMultipartFile file = new MockMultipartFile("file", "ach-payrol.txt",
                MediaType.TEXT_PLAIN_VALUE, achContent);

        mockMvc.perform(multipart("/ach/validate").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void testValidateMalformedFile() throws Exception {
        String malformed = "This is not a valid ACH file\nWith bad data";
        MockMultipartFile file = new MockMultipartFile("file", "bad.txt",
                MediaType.TEXT_PLAIN_VALUE, malformed.getBytes());

        mockMvc.perform(multipart("/ach/validate").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void testRoundTrip() throws Exception {
        // Step 1: Parse an ACH file to JSON
        byte[] achContent = Files.readAllBytes(new ClassPathResource("ach-payrol.txt").getFile().toPath());
        MockMultipartFile file = new MockMultipartFile("file", "ach-payrol.txt",
                MediaType.TEXT_PLAIN_VALUE, achContent);

        MvcResult parseResult = mockMvc.perform(multipart("/ach/parse").file(file))
                .andExpect(status().isOk())
                .andReturn();

        String jsonOutput = parseResult.getResponse().getContentAsString();
        assert jsonOutput.contains("fileHeader") : "Parse output should contain fileHeader";
        assert jsonOutput.contains("batches") : "Parse output should contain batches";

        // Step 2: Generate the file back from original content (since generate takes ACH text)
        String originalContent = new String(achContent);
        MvcResult generateResult = mockMvc.perform(post("/ach/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(originalContent))
                .andExpect(status().isOk())
                .andReturn();

        String generatedContent = generateResult.getResponse().getContentAsString();

        // Step 3: Re-parse the generated content to verify it's still valid
        MockMultipartFile regenerated = new MockMultipartFile("file", "regenerated.txt",
                MediaType.TEXT_PLAIN_VALUE, generatedContent.getBytes());
        mockMvc.perform(multipart("/ach/parse").file(regenerated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileHeader").exists())
                .andExpect(jsonPath("$.batches").isArray());
    }

    @Test
    void testParseInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "bad.txt",
                MediaType.TEXT_PLAIN_VALUE, "invalid".getBytes());

        mockMvc.perform(multipart("/ach/parse").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
