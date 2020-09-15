package com.trustly.challenge.dto;

import com.trustly.challenge.model.FileData;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class ResponseDataDto {

    HashMap<String, FileData> dataMap;
}
