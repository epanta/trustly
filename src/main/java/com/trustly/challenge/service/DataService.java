package com.trustly.challenge.service;

import com.trustly.challenge.model.FileData;

import java.util.HashMap;

public interface DataService {

    HashMap<String, FileData> findData(String user);
}
