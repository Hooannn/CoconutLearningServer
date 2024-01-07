package com.ht.elearning.search;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.elasticsearch.ElasticsearchService;
import com.ht.elearning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ElasticsearchService elasticsearchService;

    public List<User> searchRootUsers() {
        try {
            SearchResponse<User> searchResponse = elasticsearchService.searchDocuments("users", User.class);
            return searchResponse.hits().hits().stream().map(Hit::source).toList();
        } catch (IOException e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<User> lookupUsers(String q) {
        try {
            SearchResponse<User> searchResponse = elasticsearchService.lookupUsers(q);
            return searchResponse.hits().hits().stream().map(Hit::source).toList();
        } catch (IOException e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
