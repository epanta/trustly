package com.trustly.challenge.resource;

import com.trustly.challenge.dto.ResponseDataDto;
import com.trustly.challenge.exception.ProjectException;
import com.trustly.challenge.service.ScrapeService;
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
public class ScrapeController {

    private final ScrapeService scrapeService;

    @Synchronized
    @GetMapping("{userId}")
    ResponseEntity<?> findData(final @PathVariable("userId") String userId) {
        try {
            ResponseDataDto responseDataDto = ResponseDataDto
                    .builder()
                    .dataMap(scrapeService.findData(userId)).build();

            if (responseDataDto.getDataMap().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(responseDataDto);
        } catch (ProjectException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
