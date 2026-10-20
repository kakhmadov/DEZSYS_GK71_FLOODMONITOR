package com.example.floodmonitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"simulation.enabled=false"})
public class ApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testJsonSupport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stations").accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"));
    }

    @Test
    public void testXmlSupport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stations").accept("application/xml"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml"));
    }

    @Test
    public void testUnknownStation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stations/ST-999").accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Station ST-999 wurde nicht gefunden"));
    }

    @Test
    public void testUnsupportedFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stations").accept("text/html"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }
}