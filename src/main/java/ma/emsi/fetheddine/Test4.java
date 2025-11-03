package ma.emsi.fetheddine;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.time.Duration;

public class Test4 {

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {

        String apiKey = System.getenv("GEMINI_KEY");

        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .timeout(Duration.ofSeconds(60))
                .logRequestsAndResponses(true)
                .build();

        String nomDocument = "infos.txt";
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);

        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("text-embedding-004")
                .build();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(document);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .maxResults(3)
                        .build())
                .build();


        // 1. Première question
        // String question = "Comment s'appelle le chat de Pierre ?";

        // 2. Deuxième question
        // String question = "Pierre appelle son chat. Qu'est-ce qu'il pourrait dire ?";

        // 3. Troisième question (après modification de infos.txt)
        String question = "Quelle est la capitale de la France ?";

        String answer = assistant.chat(question);

        System.out.println("==================================================");
        System.out.println("Question : " + question);
        System.out.println("Réponse : " + answer);
        System.out.println("==================================================");
    }
}
