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
import java.util.Scanner;

public class Test5 {

    // L'interface pour le service IA
    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("La clé GEMINI_KEY doit être définie.");
        }

        // 1. Configurer le modèle de Chat (LLM)
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.2)
                .timeout(Duration.ofSeconds(90L))
                .logRequestsAndResponses(true)
                .build();

        // 2. Charger le document PDF
        String nomDocument = "Machine Learning.pdf";
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);

        // 3. Configurer le modèle d'Embedding
        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("text-embedding-004")
                .build();

        // 4. Créer la base de données vectorielle en mémoire
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 5. "Ingérer" le document
        EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(document);

        // 6. Créer l'Assistant AiService
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) // Ajouter une mémoire
                .contentRetriever(EmbeddingStoreContentRetriever.builder() // Le RAG
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .maxResults(8) // Récupérer les 8 morceaux les plus pertinents
                        .build())
                .build();

        // 7. Démarrer la boucle de conversation
        conversationAvec(assistant);
    }

    /**
     * Gère la conversation interactive avec l'assistant dans la console.
     */
    private static void conversationAvec(Assistant assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.println("Posez votre question (ou 'fin' pour quitter) : ");
                String question = scanner.nextLine();

                if (question.isBlank()) {
                    continue;
                }

                if ("fin".equalsIgnoreCase(question)) {
                    System.out.println("Conversation terminée.");
                    break;
                }

                System.out.println("==================================================");
                String reponse = assistant.chat(question);
                System.out.println("Assistant : " + reponse);
                System.out.println("==================================================");
            }
        }
    }
}

