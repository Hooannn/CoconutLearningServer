package com.ht.elearning.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.ht.elearning.user.User;
import com.ht.elearning.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchService.class);
    private final ElasticsearchClient elasticsearchClient;


    public <T> void indexDocument(String index, String id, T document) {
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index(index)
                    .id(id)
                    .document(document)
            );
            logger.debug("Indexed document - Response[{}]", response.toString());
        } catch (IOException e) {
            logger.error("Failed to index document - Message[{}]", e.getMessage());
        }
    }


    public <T> void indexDocuments(String index, List<T> documents, Function<T, String> getIdFunction) {
        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
            for (T document : documents) {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(index)
                                .id(getIdFunction.apply(document))
                                .document(document)
                        )
                );
            }
            BulkResponse response = elasticsearchClient.bulk(br.build());
            logger.debug("Indexed documents - Response[{}]", response.toString());
            if (response.errors()) {
                logger.error("Bulk had errors");
                for (BulkResponseItem item : response.items()) {
                    if (item.error() != null) {
                        logger.error(item.error().reason());
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to index document - Message[{}]", e.getMessage());
        }
    }


    public <T> SearchResponse<T> searchDocuments(String index, Class<T> tClass) throws IOException {
        return elasticsearchClient.search(s -> s
                        .index(index),
                tClass
        );
    }


    public SearchResponse<User> lookupUsers(String query) throws IOException {
        if (Helper.isEmail(query)) {
            return elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q ->
                                    q.term(m ->
                                            m.field("email.keyword").value(query)
                                    )
                            ),
                    User.class
            );
        } else {
            return elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q ->
                                    q.multiMatch(m -> m
                                            .fields("email", "first_name", "last_name")
                                            .query(query)
                                    )
                            ),
                    User.class
            );
        }
    }
}
