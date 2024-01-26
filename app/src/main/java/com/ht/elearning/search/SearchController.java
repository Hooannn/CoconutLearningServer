package com.ht.elearning.search;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                        ResponseMessage.OK,
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
                        ResponseMessage.OK,
                        true,
                        response
                )
        );
    }
}
