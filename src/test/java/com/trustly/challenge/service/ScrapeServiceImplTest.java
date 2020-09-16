package com.trustly.challenge.service;

import com.trustly.challenge.exception.ProjectException;
import com.trustly.challenge.exception.RepositoryDataException;
import com.trustly.challenge.service.impl.ScrapeServiceImpl;
import com.trustly.challenge.utils.ScrapeUtil;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ScrapeServiceImplTest {

    @SpyBean
    private ScrapeServiceImpl scrapeService;

    private final String user = "epanta";

    private ScrapeUtil scrapeUtil;

    @BeforeEach
    public void setUp() throws Exception {
        Mockito.doReturn(new Document("")).when(scrapeService).getDocument(Mockito.anyString());
        scrapeUtil = new ScrapeUtil();
    }

    @Test
    void shouldThrowExceptionWhenGitHubUrlConnectionIsInvalid() throws IOException {

        Mockito.doThrow(ProjectException.class).when(scrapeService).getDocument(Mockito.anyString());

        Throwable expectedMessage = Assertions.assertThrows(ProjectException.class, () -> {
            scrapeService.findData(user);
        });

       Assertions.assertEquals("Error while reading project names.", expectedMessage.getMessage());
    }

    @Test
    void shouldDoesNotThrowExceptionWhenUrlConnectionIsValid() throws IOException {
        Assertions.assertDoesNotThrow( () -> scrapeService.findData(user));
    }

    @Test
    void shouldThrowExceptionWhenScrapeRepositoryIsInvalid() throws IOException {

        Document doc = new Document("");
        doc.addClass(scrapeUtil.projectsClass);

        Mockito.doReturn(doc).when(scrapeService).getDocument(Mockito.anyString());
        Mockito.doThrow(RepositoryDataException.class).when(scrapeService).scrapeRepository(Mockito.anyString());

        Throwable expectedMessage = Assertions.assertThrows(ProjectException.class, () -> {
            scrapeService.findData(user);
        });

        Assertions.assertEquals("Error while reading repository name.", expectedMessage.getMessage());
    }

    @Test
    void shouldDoesNotThrowExceptionWhenScrapeFileIsNotFound() throws IOException {
        Document doc = new Document("");
        doc.addClass(scrapeUtil.projectsClass);
        doc.appendElement(scrapeUtil.fileAndDirectoriesClass);

        Mockito.doReturn(doc).when(scrapeService).getDocument(Mockito.anyString());

        Assertions.assertDoesNotThrow(() -> scrapeService.findData(user));
    }
}
