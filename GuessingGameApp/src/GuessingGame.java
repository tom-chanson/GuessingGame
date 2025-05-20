import java.util.Scanner;
import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class GuessingGame {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int numberToGuess = random.nextInt(100) + 1; // Génère un nombre aléatoire entre 1 et 100
        int numberOfTries = 0;
        int guess;
        boolean win = false;

        System.out.println("Bienvenue au jeu de devinettes !");
        System.out.println("J'ai choisi un nombre entre 1 et 100. Pouvez-vous le deviner ?");
        System.out.print("Entrez votre nom : ");
        String playerName = scanner.nextLine();

        while (!win) {
            System.out.print("Entrez votre devinette : ");
            guess = scanner.nextInt();
            numberOfTries++;

            if (guess < 1 || guess > 100) {
                System.out.println("Votre devinette est hors des limites. Essayez un nombre entre 1 et 100.");
            } else if (guess < numberToGuess) {
                System.out.println("Votre devinette est trop basse.");
            } else if (guess > numberToGuess) {
                System.out.println("Votre devinette est trop élevée.");
            } else {
                win = true;
            }
        }

        System.out.println("Félicitations ! Vous avez deviné le nombre " + numberToGuess + " en " + numberOfTries + " tentatives.");

        // Enregistrer le score dans la base de données
        saveScore(playerName, numberOfTries);

        scanner.close();
    }

    private static void saveScore(String playerName, int attempts) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO Scores (player_name, attempts) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerName);
                statement.setInt(2, attempts);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
