package com.ht.elearning.search;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ht.elearning.config.Response;
import com.ht.elearning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/search")
@CrossOrigin
public class SearchController {
    private final SearchService searchService;

    @GetMapping("users")
    public ResponseEntity<Response<List<User>>> searchRootUsers() {
        var response = searchService.searchRootUsers();
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        response
                )
        );
    }


    @GetMapping("users/lookup")
    public ResponseEntity<Response<List<User>>> lookupUsers(@RequestParam String q) {
        var response = searchService.lookupUsers(q);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Ok",
                        true,
                        response
                )
        );
    }
}
