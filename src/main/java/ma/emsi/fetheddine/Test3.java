package ma.emsi.fetheddine;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;

public class Test3 {
    public static void main(String[] args) {

        String cle = System.getenv("GEMINI_KEY");

        EmbeddingModel modele = GoogleAiEmbeddingModel.builder()
                .apiKey(cle)
                .modelName("text-embedding-004")
                .taskType(GoogleAiEmbeddingModel.TaskType.SEMANTIC_SIMILARITY)
                .outputDimensionality(300)
                .timeout(Duration.ofSeconds(100))
                .build();

        String[][] couples = {
                {"La gestion de projet est cruciale pour le succès.", "Le management de projet est essentiel pour réussir."}, // Presque similaire
                {"L'optimisation des requêtes SQL est importante.", "Une base de données bien conçue améliore les performances."}, // Moyenne
                {"La sécurité des réseaux est un domaine complexe.", "J'aime la cuisine italienne."} // Pas similaire
        };

        for (String[] couple : couples) {
            Response<Embedding> reponse1 = modele.embed(couple[0]);
            Response<Embedding> reponse2 = modele.embed(couple[1]);

            Embedding emb1 = reponse1.content();
            Embedding emb2 = reponse2.content();

            double similarite = CosineSimilarity.between(emb1, emb2);
            System.out.printf("Similarité cosinus entre \"%s\" et \"%s\" : %.4f%n",
                    couple[0], couple[1], similarite);
        }
    }
}

