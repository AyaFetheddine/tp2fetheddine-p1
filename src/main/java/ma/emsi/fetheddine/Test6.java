package ma.emsi.fetheddine;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import ma.emsi.fetheddine.tools.meteo.MeteoTool;

import java.time.Duration;
import java.util.Scanner;

public class Test6 {

    interface AssistantMeteo {
        String repondre(String userMessage);
    }

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("La clé GEMINI_KEY doit être définie.");
        }

        // 1. Créer le modèle de Chat, avec le logging activé
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.2)
                .timeout(Duration.ofSeconds(60L))
                .logRequestsAndResponses(true)
                .build();

        // 2. Créer l'assistant IA
        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatModel(model)
                .tools(new MeteoTool())
                .build();

        // 3. Démarrer la conversation
        conversationAvec(assistant);
    }


    private static void conversationAvec(AssistantMeteo assistant) {
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
                String reponse = assistant.repondre(question);
                System.out.println("Assistant : " + reponse);
                System.out.println("==================================================");
            }
        }
    }
}
