package com.trustly.challenge.service.impl;

import com.trustly.challenge.exception.BusinessException;
import com.trustly.challenge.model.FileData;
import com.trustly.challenge.service.ScrapeService;
import com.trustly.challenge.utils.ScrapeUtil;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ScrapeServiceImpl implements ScrapeService {

    private final ScrapeUtil scrapeUtil;
    private HashMap<String, FileData> dataMap;

    @Override
    public HashMap<String, FileData> findData(final String user) {
        dataMap = new HashMap<>();

        try {
            final String newUrl = scrapeUtil.getUrlConnection(user);

            final Document doc = Jsoup.connect(newUrl).get();
            final Elements repositories = doc.getElementsByClass(scrapeUtil.projectsClass);

            for (Element repository : repositories) {
                final String projectName = repository.attr(scrapeUtil.HREF);
                scrapeRepository(projectName);
            }
        } catch (Exception e) {
            throw new BusinessException("Error while reading project names.");
        }
        return dataMap;
    }

    private void scrapeRepository(String urlBase) {
        final String newUrlBase = scrapeUtil.getUrlConnection(urlBase);

        try {
            final Document doc = Jsoup.connect(newUrlBase).get();
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
            throw new BusinessException("Error while reading file names.");
        }
    }

    private void scrapeFile(final String fileName, final String filePathName) {
        final String newUrlBase = scrapeUtil.getUrlConnection(filePathName);

        try {
            final Document doc = Jsoup.connect(newUrlBase).get();
            final String linesAndSize = doc.getElementsByClass(scrapeUtil.dataFileClass).text();

            addOrUpdateData(fileName, linesAndSize);
        } catch (Exception e) {
            throw new BusinessException("Error while reading data file.");
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
}
