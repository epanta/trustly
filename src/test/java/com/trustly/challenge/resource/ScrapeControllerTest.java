package com.trustly.challenge.resource;

import com.trustly.challenge.exception.ProjectException;
import com.trustly.challenge.model.FileData;
import com.trustly.challenge.service.ScrapeService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ScrapeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ScrapeService scrapeService;

    private static String URI = "/api/github/epanta";
    private static String URI_REPO = "/api/github?url=https://github.com/epanta/trustly";

    @Test
    public void shouldBeNotFoundWhenSearchDataByInvalidUserWhenFindDataByUser() throws Exception {

        BDDMockito.given(this.scrapeService.findDataByUser(Mockito.anyString())).willReturn(new HashMap<>());

        mvc.perform(MockMvcRequestBuilders.get(URI)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldBeNotFoundWhenSearchDataByInvalidUserWhenFindDataByUrl() throws Exception {

        BDDMockito.given(this.scrapeService.findDataByUrl(Mockito.anyString())).willReturn(new HashMap<>());

        mvc.perform(MockMvcRequestBuilders.get(URI_REPO)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldSearchDataByInvalidUserWhenFindDataByUser() throws Exception {

        BDDMockito.given(this.scrapeService.findDataByUser(Mockito.anyString())).willThrow(ProjectException.class);

        mvc.perform(MockMvcRequestBuilders.get(URI)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldSearchDataByInvalidUserWhenFindDataByUrl() throws Exception {

        BDDMockito.given(this.scrapeService.findDataByUrl(Mockito.anyString())).willThrow(ProjectException.class);

        mvc.perform(MockMvcRequestBuilders.get(URI_REPO)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldBeOkWhenSearchDataByValidUser() throws Exception {

        HashMap<String, FileData> fileDataHashMap = new HashMap<>();
        fileDataHashMap.put(".csv", new FileData());

        BDDMockito.given(this.scrapeService.findDataByUser(Mockito.anyString())).willReturn(fileDataHashMap);

        mvc.perform(MockMvcRequestBuilders.get(URI)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldBeOkWhenSearchDataByValidUserWhenFindDataByUrl() throws Exception {

        HashMap<String, FileData> fileDataHashMap = new HashMap<>();
        fileDataHashMap.put(".csv", new FileData());

        BDDMockito.given(this.scrapeService.findDataByUrl(Mockito.anyString())).willReturn(fileDataHashMap);

        mvc.perform(MockMvcRequestBuilders.get(URI_REPO)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
