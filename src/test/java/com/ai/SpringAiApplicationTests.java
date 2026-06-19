package com.ai;

import com.ai.controller.ChatController;
import org.junit.jupiter.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "spring.ai.openai.api-key = ${OPEN_AI_KEY:test-key}",
        "logging.level.org.springframework.ai=DEBUG"
})
public class SpringAiApplicationTests {

    @Autowired
    private ChatController chatController;
    @Autowired
    @Qualifier("gemmaModel")
    private ChatModel chatModel;
    private ChatClient chatClient;
    private RelevancyEvaluator relevancyEvaluator;
    private FactCheckingEvaluator factCheckingEvaluator;

    @Value("classpath:/promptTemplates/factcheck.st")
    Resource factCheckTemplate;

    @Value("classpath:/promptTemplates/hrPolicy.st")
    Resource hrPolicyTemplate;

    @Value("${test.relevancy.min-score:0.7}")
    private float minRelevancyScore;

    @BeforeEach
    void setup() throws IOException {
        ChatClient.Builder chatBuilder = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor());
        this.chatClient = chatBuilder.build();
        this.relevancyEvaluator = new RelevancyEvaluator(chatBuilder);
        this.factCheckingEvaluator = FactCheckingEvaluator.builder(chatBuilder)
                .evaluationPrompt(factCheckTemplate.getContentAsString(Charset.defaultCharset()))
                .build();
    }

    @Test
    @DisplayName("Should return relevant response for basic geography question")
    @Timeout(value = 300)
    void evaluateChatControllerResponseRelevancy(){
        String question = "What is the capital of India?";
        String aiResponse = chatController.gemmaChat(question).getBody();
        EvaluationRequest evaluationRequest = new EvaluationRequest(question,aiResponse);
        EvaluationResponse evaluationResponse =  relevancyEvaluator.evaluate(evaluationRequest);
        Assertions.assertAll(() -> assertThat(aiResponse).isNotBlank(),
                () -> assertThat(evaluationResponse.isPass())
                        .withFailMessage("""
                                ========================================
                                The answer was not considered relevant.
                                Question: "%s"
                                Response: "%s"
                                ========================================
                                """, question, aiResponse).isTrue(),
                () -> assertThat(evaluationResponse.getScore())
                        .withFailMessage("""
                                ========================================
                                The score %.2f is lower than the minimum required %.2f.
                                Question: "%s"
                                Response: "%s"
                                ========================================
                                """, evaluationResponse.getScore(), minRelevancyScore, question, aiResponse)
                        .isGreaterThan(minRelevancyScore)
        );
    }

    @Test
    @DisplayName("Should return factually correct response for gravity-related question")
    @Timeout(value = 300)
    void evaluateFactAccuracyForGravityQuestion() {
        // Given
        String question = "Who discovered the law of universal gravitation?";

        // When
        String aiResponse = chatController.gemmaChat(question).getBody();
        EvaluationRequest evaluationRequest = new EvaluationRequest(question, aiResponse);
        EvaluationResponse response = factCheckingEvaluator.evaluate(evaluationRequest);

        // Then
        Assertions.assertAll(
                () -> assertThat(aiResponse).isNotBlank(),
                () -> assertThat(response.isPass())
                        .withFailMessage("""
                        ========================================
                        The response was not considered factually accurate.
                        Question: %s
                        Response: %s
                        Context: %s
                        ========================================
                        """, question, aiResponse, "")
                        .isTrue());

    }

    @Test
    @DisplayName("Should correctly evaluate factual response based on HR policy context (RAG scenario)")
    @Timeout(value = 300)
    public void evaluateHrPolicyAnswerWithRagContext() throws IOException {
        // Given
        String question = "How many paid leaves do employees get annually?";

        // When
        String aiResponse = chatController.promptStuffing(question);
        String retrievedContext = hrPolicyTemplate.getContentAsString(Charset.defaultCharset());

        EvaluationRequest evaluationRequest = new EvaluationRequest(
                question, List.of(new Document(retrievedContext)),
                aiResponse
        );

        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        // Then
        Assertions.assertAll(
                () -> assertThat(aiResponse).isNotBlank(),
                () -> assertThat(evaluationResponse.isPass())
                        .withFailMessage("""
                        ========================================
                        The response was not considered factually accurate.
                        Question: %s
                        Response: %s
                        Context: %s
                        ========================================
                        """, question, aiResponse, retrievedContext)
                        .isTrue());
    }
}
