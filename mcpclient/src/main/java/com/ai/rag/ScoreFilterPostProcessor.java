package com.ai.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScoreFilterPostProcessor implements DocumentPostProcessor {


    @Override
    public List<Document> process(Query query, List<Document> documents) {
        return documents.stream().filter(f->f.getScore()>0.6).toList();
    }
}
