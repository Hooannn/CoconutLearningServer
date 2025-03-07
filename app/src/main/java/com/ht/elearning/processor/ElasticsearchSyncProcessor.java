package com.ht.elearning.processor;

import com.ht.elearning.elasticsearch.ElasticsearchService;
import com.ht.elearning.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchSyncProcessor {
    private final ElasticsearchService elasticsearchService;

    @Async
    public void userDidSave(User savedUser) {
        elasticsearchService.indexDocument("users", savedUser.getId(), savedUser);
    }

    @Async
    public void indexUsers(List<User> users) {
        elasticsearchService.indexDocuments("users", users, User::getId);
    }

    public void deleteUserDocuments() throws IOException {
        elasticsearchService.deleteDocuments("users");
    }
}
