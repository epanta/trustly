package com.trustly.challenge.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ScrapeUtil {

    public static final String HREF = "href";
    public static final String projectsClass = "text-bold flex-auto min-width-0";
    public static final String fileAndDirectoriesClass = "js-navigation-open link-gray-dark";
    public static final String dataFileClass = "text-mono f6 flex-auto pr-3 flex-order-2 flex-md-order-1 mt-2 mt-md-0";
    static final String URL_GIT = "https://github.com";
    static final String separator = "/";

    public String getUrlConnection(final String path) {
        StringBuilder stringBuilder = new StringBuilder(URL_GIT);
        stringBuilder.append(separator);
        stringBuilder.append(path);
        return stringBuilder.toString();
    }

    public String getExtension(final String fileName) {
        String extension = "";

        final String[] name = fileName.split("\\.");
        if (name != null) {
            extension = "." + name[name.length - 1].trim();
        }
        return extension;
    }

    public Integer getLines(final String line) {
        Integer totalLines = 0;

        if (!StringUtils.isEmpty(line)
                && line.contains("lines")) {

            final String[] lineSplited = line.split("lines");

            if (lineSplited != null && lineSplited.length > 0) {
                String total = lineSplited[0];
                if (!StringUtils.isEmpty(total)) {
                    totalLines = Integer.valueOf(total.trim());
                }
            }
        }
        return totalLines;
    }

    public BigDecimal getSize(final String line) {
        BigDecimal totalSize = BigDecimal.ZERO;

        if (!StringUtils.isEmpty(line)) {
            final String[] lineSplited = line.split("sloc\\)");

            if (lineSplited != null && lineSplited.length > 1) {
                String total = lineSplited[1];
                if (!StringUtils.isEmpty(total)) {
                    totalSize = getSplitedSize(total.trim());
                } else {
                    totalSize = getSplitedSize(line.trim());
                }
            } else {
                totalSize = getSplitedSize(line.trim());
            }
        }
        return totalSize;
    }

    BigDecimal getSplitedSize(final String line) {
        BigDecimal totalLines = BigDecimal.ZERO;

        if (!StringUtils.isEmpty(line)) {
            final String[] lineSplited = line.split(" ");
            if (lineSplited != null && lineSplited.length > 0) {
                final String total = lineSplited[0];
                final BigDecimal multiplyFactor = getMultiplyFactor(lineSplited);

                if (!StringUtils.isEmpty(total)) {
                    totalLines = new BigDecimal(total.trim()).multiply(multiplyFactor);
                }
            }
        }
        return totalLines;
    }

    private BigDecimal getMultiplyFactor(final String[] lineSplited) {
        BigDecimal multiplyFactor = BigDecimal.ONE;

        if (lineSplited.length > 1) {
            final String unit = lineSplited[1].trim().toUpperCase();

            if (!StringUtils.isEmpty(unit)) {

                switch (unit) {
                    case "KB":
                        multiplyFactor = multiplyFactor.multiply(BigDecimal.valueOf(1000));
                        break;
                    case "MB":
                        multiplyFactor = multiplyFactor.multiply(BigDecimal.valueOf(1000000));
                        break;
                    case "GB":
                        multiplyFactor = multiplyFactor.multiply(BigDecimal.valueOf(1e+9));
                        break;
                    case "TB":
                        multiplyFactor = multiplyFactor.multiply(BigDecimal.valueOf(1e+12));
                        break;
                    case "PB":
                        multiplyFactor = multiplyFactor.multiply(BigDecimal.valueOf(1e+15));
                        break;
                }
            }
        }

        return multiplyFactor;
    }

}
