package com.trustly.challenge.service.impl;

import com.trustly.challenge.model.FileData;
import com.trustly.challenge.service.DataService;
import com.trustly.challenge.utils.ScraperUtil;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {

    HashMap<String, FileData> map = new HashMap<>();

    private final ScraperUtil scraperUtil;

    @Override
    public HashMap<String, FileData> findData(final String user) {

        String newUrl = scraperUtil.getUrlConnection(user);

        try {
            Document doc = Jsoup.connect(newUrl).get();
            Elements repositories = doc.getElementsByClass(scraperUtil.projectsClass);

            for (Element repository : repositories) {
                String projectName = repository.attr(scraperUtil.HREF);
                scrapeRepository(projectName);
            }
            printData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void scrapeRepository(String urlBase) {
        final String newUrlBase = scraperUtil.getUrlConnection(urlBase);

        try {
            Document doc = Jsoup.connect(newUrlBase).get();
            Elements details = doc.getElementsByClass(scraperUtil.fileAndDirectoriesClass);

            for (Element detail : details) {
                Elements files = detail.getElementsByAttribute(scraperUtil.HREF);

                for (Element file : files) {
                    String filePathName = file.attr(scraperUtil.HREF);

                    if (!filePathName.contains("commit")) {
                        if (filePathName.contains("tree")) {
                            System.out.println("Folder Name: " + scraperUtil.getUrlConnection(filePathName));
                            scrapeRepository(filePathName);
                        } else if (filePathName.contains("blob")) {
                            scrapeFile(file.ownText(), filePathName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scrapeFile(final String fileName, final String filePathName) {
        final String newUrlBase = scraperUtil.getUrlConnection(filePathName);

        try {
            final Document doc = Jsoup.connect(newUrlBase).get();
            final String linesAndSize = doc.getElementsByClass(scraperUtil.dataFileClass).text();

            addOrUpdateData(fileName, linesAndSize);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addOrUpdateData(final String fileName, final String linesAndSize) {
        final String ext = scraperUtil.getExtension(fileName);
        final Integer lines = scraperUtil.getLines(linesAndSize);
        final BigDecimal size = scraperUtil.getSize(linesAndSize);

        map.put(ext, FileData.builder()
                .totalBytes(size)
                .totalLines(lines).build());
    }

    private void printData() {
        for (Map.Entry<String, FileData> entry : map.entrySet()) {
            String key = entry.getKey();
            FileData value = entry.getValue();
            System.out.println(String.format("Extension: %s | Lines: %s | Size: %s", key, value.getTotalLines(), value.getTotalBytes()));
        }
    }
}
