package com.trustly.challenge.service;

import com.trustly.challenge.exception.BusinessException;
import com.trustly.challenge.model.FileData;
import com.trustly.challenge.utils.ScrapeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ScrapeServiceImplTest {

    @MockBean
    private ScrapeUtil scrapeUtil;
    private HashMap<String, FileData> dataMap;

    @Autowired
    private ScrapeService scrapeService;

    @BeforeEach
    public void setUp() throws Exception {
        BDDMockito.given(this.scrapeUtil.getUrlConnection(Mockito.anyString())).willThrow(BusinessException.class);
    }

    @Test
    void shouldThrowExceptionWhenUrlConnectionIsInvalid() {
        String user = "epanta";

        Throwable expectedMessage = Assertions.assertThrows(BusinessException.class, () -> {
            scrapeService.findData(user);
        });

       Assertions.assertEquals("Error while reading project names.", expectedMessage.getMessage());
    }
}
