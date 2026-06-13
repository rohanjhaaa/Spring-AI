package com.ai.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HRPolicyLoader {
    private final VectorStore vectorStore;

    @Value("classpath:RJ_Group_HR_Policy_Manual.pdf")
    Resource hrPolicyDoc;

    public HRPolicyLoader(VectorStore vectorStore){
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void loadPDF(){
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(hrPolicyDoc);
        List<Document> documentList = tikaDocumentReader.get();
        // split docs
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(5)
                .withMaxNumChunks(10000)
                .build();
        List<Document> chunks = splitter.apply(documentList);
        System.out.println("Original docs : " + documentList.size());
        System.out.println("Chunks : " + chunks.size());
        vectorStore.add(chunks);
    }
}
