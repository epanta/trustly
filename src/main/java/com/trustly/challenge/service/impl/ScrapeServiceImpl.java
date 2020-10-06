package com.trustly.challenge.service.impl;

import com.trustly.challenge.exception.FileDataException;
import com.trustly.challenge.exception.ProjectException;
import com.trustly.challenge.exception.RepositoryDataException;
import com.trustly.challenge.model.FileData;
import com.trustly.challenge.service.ScrapeService;
import com.trustly.challenge.utils.ScrapeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapeServiceImpl implements ScrapeService {

    private final ScrapeUtil scrapeUtil;
    private HashMap<String, FileData> dataMap;

    @Override
    public HashMap<String, FileData> findDataByUser(final String user) {
        final String newUrl = scrapeUtil.getUrlConnection(user,
                "?tab=repositories");
        return getFindData(newUrl, user, null);
    }

    @Override
    public HashMap<String, FileData> findDataByUrl(final String url) {
        final String user = scrapeUtil.getUser(url);
        final String newUrl = scrapeUtil.getUrlConnection(user,
                "?tab=repositories");
        return getFindData(newUrl, user, scrapeUtil.getRepositorio(url));
    }

    private HashMap<String, FileData> getFindData(final String newUrl,
                                                  final String user,
                                                  final String repo) {
        dataMap = new HashMap<>();
        try {
            final Document doc = getDocument(newUrl);
            final Elements repositories = doc.getElementsByClass(scrapeUtil.projectsClass);

            for (Element repository : repositories) {
                final String projectName = repository.text();
                if (scrapeUtil.isValidRepository(projectName, repo)) {
                    log.info("m=getFindData, finding data project={}", projectName);
                    scrapeRepository(scrapeUtil.getUri(user, projectName));
                }
            }
        } catch (RepositoryDataException e) {
            throw new ProjectException("Error while reading repository name.", e);
        } catch (FileDataException e) {
            throw new ProjectException("Error while reading file names.", e);
        } catch (Exception e) {
            throw new ProjectException("Error while reading project names.", e);
        }
        return dataMap;
    }

    public void scrapeRepository(final String uriBase) {
        final String newUrlBase = scrapeUtil.getUrlConnection(uriBase);

        try {
            final Document doc = getDocument(newUrlBase);
            final Elements details = doc.getElementsByClass(scrapeUtil.fileAndDirectoriesClass);

            for (Element detail : details) {
                final Elements files = detail.getElementsByAttribute(scrapeUtil.HREF);

                for (Element file : files) {
                    final String filePathName = file.attr(scrapeUtil.HREF);

                    if (!filePathName.contains("commit")) {
                        if (filePathName.contains("tree")) {
                            scrapeRepository(filePathName);
                        } else if (filePathName.contains("blob")) {
                            scrapeFile(file.ownText(), filePathName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RepositoryDataException();
        }
    }

    public void scrapeFile(final String fileName, final String filePathName) {
        final String newUrlBase = scrapeUtil.getUrlConnection(filePathName);

        try {
            final Document doc = getDocument(newUrlBase);
            final String linesAndSize = doc.getElementsByClass(scrapeUtil.dataFileClass).text();

            addOrUpdateData(fileName, linesAndSize);
        } catch (Exception e) {
            throw new FileDataException();
        }
    }

    private void addOrUpdateData(final String fileName, final String linesAndSize) {
        final String ext = scrapeUtil.getExtension(fileName);
        final Integer lines = scrapeUtil.getLines(linesAndSize);
        final BigDecimal size = scrapeUtil.getSize(linesAndSize);

        dataMap.put(ext, FileData.builder()
                .totalBytes(size)
                .totalLines(lines).build());
    }

    public Document getDocument(final String newUrl) throws IOException {
        return Jsoup.connect(newUrl).get();
    }
}
