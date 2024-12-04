package com.ht.elearning.search;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.user.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/search")
@CrossOrigin
public class SearchController {
    private final SearchService searchService;

    @Operation(summary = "Lookup users by their name or email")
    @GetMapping("users/lookup")
    public ResponseEntity<Response<List<User>>> lookupUsers(@RequestParam String q) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getPrincipal().toString();
        var response = searchService.lookupUsers(q).stream().filter(user -> !user.getId().equals(userId)).toList();
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
