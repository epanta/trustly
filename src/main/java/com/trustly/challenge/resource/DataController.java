package com.trustly.challenge.resource;

import com.trustly.challenge.dto.ResponseDataDto;
import com.trustly.challenge.service.DataService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/github")
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @GetMapping("{userId}")
    @Synchronized
    ResponseEntity<ResponseDataDto> findData(final @PathVariable("userId") String userId) {

        ResponseDataDto responseDataDto = ResponseDataDto
                                                .builder()
                                                .dataMap(dataService.findData(userId)).build();
        return ResponseEntity.ok(responseDataDto);
    }
}
