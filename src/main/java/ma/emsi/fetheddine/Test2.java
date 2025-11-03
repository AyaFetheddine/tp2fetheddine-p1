package ma.emsi.fetheddine;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.time.Duration;
import java.util.Map;

public class Test2 {
    public static void main(String[] args) {
        PromptTemplate template = PromptTemplate.from(
                "Traduis le texte suivant en anglais : {{texte}}"
        );

        Prompt prompt = template.apply(Map.of(
                "texte", "Nous devons finaliser le rapport trimestriel avant la réunion de demain."
        ));

        String cle = System.getenv("GEMINI_KEY");
        ChatModel modele =
                GoogleAiGeminiChatModel.builder()
                        .apiKey(cle)
                        .modelName("gemini-2.5-flash")
                        .temperature(0.7)
                        .timeout(Duration.ofSeconds(60))
                        .responseFormat(ResponseFormat.JSON)
                        .build();

        ChatRequest requete = ChatRequest.builder()
                .messages(UserMessage.from(prompt.text()))
                .build();

        ChatResponse reponse = modele.chat(requete);
        System.out.println(reponse.aiMessage().text());
    }
}

