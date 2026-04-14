package com.afrunt.jach.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ACHControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testParseEndpoint() throws Exception {
        try (InputStream is = new ClassPathResource("ach-payrol.txt").getInputStream()) {
            MockMultipartFile file = new MockMultipartFile("file", "ach-payrol.txt", "text/plain", is);

            mockMvc.perform(multipart("/ach/parse").file(file))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fileHeader").exists())
                    .andExpect(jsonPath("$.batches").exists())
                    .andExpect(jsonPath("$.rawContent").exists());
        }
    }

    @Test
    void testGenerateEndpoint() throws Exception {
        try (InputStream is = new ClassPathResource("ach-payrol.txt").getInputStream()) {
            String nachaContent = new String(is.readAllBytes());

            String json = "{\"rawContent\": " + escapeJson(nachaContent) + "}";

            mockMvc.perform(post("/ach/generate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void testValidateEndpoint() throws Exception {
        try (InputStream is = new ClassPathResource("ach-payrol.txt").getInputStream()) {
            MockMultipartFile file = new MockMultipartFile("file", "ach-payrol.txt", "text/plain", is);

            mockMvc.perform(multipart("/ach/validate").file(file))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(true));
        }
    }

    private String escapeJson(String value) {
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                + "\"";
    }
}
