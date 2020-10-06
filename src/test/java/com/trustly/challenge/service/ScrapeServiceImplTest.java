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
    private final String url = "https://github.com/epanta/trustly";

    @SpyBean
    private ScrapeUtil scrapeUtil;

    @BeforeEach
    public void setUp() throws Exception {
        Mockito.doReturn(new Document("")).when(scrapeService).getDocument(Mockito.anyString());
    }

    @Test
    void shouldThrowExceptionWhenGitHubUrlConnectionIsInvalidWhenFindByUser() throws IOException {

        Mockito.doThrow(ProjectException.class).when(scrapeService).getDocument(Mockito.anyString());

        Throwable expectedMessage = Assertions.assertThrows(ProjectException.class, () -> {
            scrapeService.findDataByUser(user);
        });

       Assertions.assertEquals("Error while reading project names.", expectedMessage.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenGitHubUrlConnectionIsInvalidWhenFindByUrl() throws IOException {

        Mockito.doThrow(ProjectException.class).when(scrapeService).getDocument(Mockito.anyString());

        Throwable expectedMessage = Assertions.assertThrows(ProjectException.class, () -> {
            scrapeService.findDataByUrl(url);
        });

        Assertions.assertEquals("Error while reading project names.", expectedMessage.getMessage());
    }

    @Test
    void shouldDoesNotThrowExceptionWhenUrlConnectionIsValidWhenFindByUser() throws IOException {
        Assertions.assertDoesNotThrow( () -> scrapeService.findDataByUser(user));
    }

    @Test
    void shouldDoesNotThrowExceptionWhenUrlConnectionIsValidWhenFindByUrl() throws IOException {
        Assertions.assertDoesNotThrow( () -> scrapeService.findDataByUrl(url));
    }

    @Test
    void shouldThrowExceptionWhenScrapeRepositoryIsInvalidWhenFindByUser() throws IOException {

        Document doc = new Document("");
        doc.addClass(scrapeUtil.projectsClass);

        Mockito.doReturn(doc).when(scrapeService).getDocument(Mockito.anyString());
        Mockito.doThrow(RepositoryDataException.class).when(scrapeService).scrapeRepository(Mockito.anyString());

        Throwable expectedMessage = Assertions.assertThrows(ProjectException.class, () -> {
            scrapeService.findDataByUser(user);
        });

        Assertions.assertEquals("Error while reading repository name.", expectedMessage.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenScrapeRepositoryIsInvalidWhenFindByUrl() throws IOException {

        Document doc = new Document("");
        doc.addClass(scrapeUtil.projectsClass);

        Mockito.doReturn(doc).when(scrapeService).getDocument(Mockito.anyString());
        Mockito.doReturn(true).when(scrapeUtil).isValidRepository(Mockito.anyString(), Mockito.anyString());
        Mockito.doThrow(RepositoryDataException.class).when(scrapeService).scrapeRepository(Mockito.anyString());

        Throwable expectedMessage = Assertions.assertThrows(ProjectException.class, () -> {
            scrapeService.findDataByUrl(url);
        });

        Assertions.assertEquals("Error while reading repository name.", expectedMessage.getMessage());
    }

    @Test
    void shouldDoesNotThrowExceptionWhenScrapeFileIsNotFound() throws IOException {
        Document doc = new Document("");
        doc.addClass(scrapeUtil.projectsClass);
        doc.appendElement(scrapeUtil.fileAndDirectoriesClass);

        Mockito.doReturn(doc).when(scrapeService).getDocument(Mockito.anyString());

        Assertions.assertDoesNotThrow(() -> scrapeService.findDataByUser(user));
    }
}
