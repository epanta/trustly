package com.trustly.challenge.service;

import com.trustly.challenge.model.FileData;

import java.util.HashMap;

public interface ScrapeService {

    HashMap<String, FileData> findDataByUser(String user);

    HashMap<String, FileData> findDataByUrl(String url);
}
