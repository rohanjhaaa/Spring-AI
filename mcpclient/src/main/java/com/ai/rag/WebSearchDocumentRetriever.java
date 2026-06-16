package com.ai.rag;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

public class WebSearchDocumentRetriever implements DocumentRetriever {

    private static final Logger logger = LoggerFactory.getLogger(WebSearchDocumentRetriever.class);

    private final String baseUrl;
    private final String apiKey;

    private static final int DEFAULT_RESULT_LIMIT=5;
    private final int resultLimit;
    private final RestClient restClient;

    public WebSearchDocumentRetriever(RestClient.Builder clientBuilder, int resultLimit, String baseUrl, String apiKey){
        this.restClient = clientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer "+apiKey)
                .build();
        if(resultLimit<=0){
            throw new IllegalArgumentException("result limit must be greater than 0");
        }
        this.resultLimit = resultLimit;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public List<Document> retrieve(Query query) {
        logger.info("Processing query: {}", query.text());
        Assert.notNull(query, "query cannot be null");

        String q = query.text();
        Assert.hasText(q,"query.text() cannot be empty");

        TavilyResponsePayload response = restClient.post()
                .body(new TavilyRequestPayload(q,"advanced", resultLimit))
                .retrieve()
                .body(TavilyResponsePayload.class);

        if(response == null || CollectionUtils.isEmpty(response.results)){
            return List.of();
        }

        List<Document> docs = new ArrayList<>(response.results().size());

        for (TavilyResponsePayload.HIT hit : response.results()){
            Document doc = Document.builder()
                    .text(hit.content)
                    .metadata("title",hit.title())
                    .metadata("url", hit.url())
                    .score(hit.score())
                    .build();
            docs.add(doc);
        }
        return docs;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record TavilyRequestPayload(String query, String depthSearch, int maxResult){}

    record TavilyResponsePayload(List<HIT> results){
        record HIT(String title, String url, String content, Double score){}
    }

    public static Builder builder() {
        return new Builder();
    }
    public static class Builder{
        private RestClient.Builder clientBuilder;
        private int resultLimit = DEFAULT_RESULT_LIMIT;
        private String baseUrl;
        private String apiKey;
        private Builder(){}

        public Builder restClientBuilder(RestClient.Builder clientBuilder){
            this.clientBuilder = clientBuilder;
            return this;
        }

        public Builder maxResults(int maxResult){
            this.resultLimit = maxResult;
            return this;
        }
        public Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(String apiKey){
            this.apiKey = apiKey;
            return this;
        }
        public WebSearchDocumentRetriever build(){
            return new WebSearchDocumentRetriever(clientBuilder,resultLimit,baseUrl,apiKey);
        }

    }

}
