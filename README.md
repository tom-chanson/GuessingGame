# GuessingGame

GuessingGame est une application Java ludique où les joueurs doivent deviner un nombre aléatoire 
choisi par l'ordinateur. Le jeu est simple et interactif, conçu pour être à la fois divertissant 
et engageant.

# Fonctionnalités principales

- Génération d'un nombre aléatoire :
Au démarrage du jeu, l'ordinateur génère un nombre aléatoire compris entre 1 et 100.

- Interaction avec le joueur :
Le joueur est invité à entrer son nom au début du jeu.
Le joueur doit ensuite deviner le nombre en entrant des suppositions.

- Feedback en temps réel :
Après chaque tentative, le jeu fournit un retour instantané indiquant si la supposition du joueur est trop basse, trop haute, ou correcte.

- Fin du jeu :
Le jeu continue jusqu'à ce que le joueur devine correctement le nombre.
Une fois le nombre deviné, le jeu affiche un message de félicitations et le nombre de tentatives effectuées.

- Enregistrement des scores :
Les scores des joueurs (nom et nombre de tentatives) sont enregistrés dans une base de données MySQL pour un suivi et une analyse futurs.
Les informations de connexion à la base de données sont lues depuis un fichier .env pour des raisons de sécurité.



# Préparer la base de données

Installer MySQL : Assurez-vous que MySQL est installé et en cours d'exécution sur votre machine.

Créer la base de données et la table :

```
CREATE DATABASE GuessingGameDB;

USE GuessingGameDB;

CREATE TABLE Scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(50),
    attempts INT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

```


# Télécharger le Connecteur MySQL

Étape 1 : Télécharger le Connecteur MySQL

- Rendez-vous sur la page de téléchargement du [Mysql Connnector]("https://dev.mysql.com/downloads/connector/j/").

- Téléchargez le fichier ZIP et extrayez-le. Vous y trouverez le fichier JAR nécessaire, par exemple **mysql-connector-java-8.0.29.jar.**

Étape 2 : Ajouter le JAR au Classpath

Lorsque vous compilez et exécutez votre programme, vous devrez inclure le **JAR du connecteur MySQL dans le classpath**. 
Voici comment le faire :

```
javac -cp .;path\to\mysql-connector-java-8.0.29.jar GuessingGame.java

```
**Remplacez path\to\ par le chemin réel où se trouve le fichier JAR sur votre système.**

Exécution :

```
java -cp .;path\to\mysql-connector-java-8.0.29.jar GuessingGame

```

Pour améliorer la sécurité et la gestion des configurations. 
Pour cela, vous aurez besoin d'une bibliothèque comme [dotenv-java]("https://github.com/cdimascio/dotenv-java") 
pour lire les variables d'environnement depuis un fichier .env.

Créez un fichier nommé **.env** **dans le même répertoire que votre fichier Java.** Ajoutez-y les variables d'environnement nécessaires :

```
DB_URL=jdbc:mysql://localhost:3306/GuessingGameDB
DB_USER=your_username
DB_PASSWORD=your_password

```
# Code Java complet avec JDBC

```

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


```
# Compilation et Exécution
Vous devez inclure à la fois le connecteur MySQL et dotenv-java dans le classpath :

Compilation :

```
javac -cp .;path\to\mysql-connector-java-8.0.29.jar;path\to\dotenv-java-5.2.2.jar GuessingGame.java

```

Exécution :

```
java -cp .;path\to\mysql-connector-java-8.0.29.jar;path\to\dotenv-java-5.2.2.jar GuessingGame

```

Remplacez **path\to\ par le chemin réel des fichiers JAR sur votre système.**


Explication des tests :

Setup de la base de données (setupDatabase) :
Avant d'exécuter les tests, cette méthode crée la table Scores si elle n'existe pas déjà.

- Nettoyage de la base de données (clearDatabase) :
Avant chaque test, cette méthode supprime toutes les entrées de la table Scores pour assurer un état propre pour chaque test.

- Test de la méthode saveScore (testSaveScore) :
Insère un score pour un joueur de test.
Vérifie que le score est correctement inséré dans la base de données en effectuant une requête de sélection.
Utilise des assertions pour vérifier que les données insérées correspondent aux attentes.  

# Compilation :

```
javac -cp .;path\to\mysql-connector-java-8.0.29.jar;path\to\junit-5.7.1.jar;path\to\hamcrest-core-1.3.jar GuessingGameTest.java

```
# Exécution :

```
java -cp .;path\to\mysql-connector-java-8.0.29.jar;path\to\junit-5.7.1.jar;path\to\hamcrest-core-1.3.jar org.junit.jupiter.api.Test GuessingGameTest

```

# Note :

*path\to* doit être remplacé par le chemin réel vers les JARs sur votre système.
La méthode saveScore dans GuessingGame doit être rendue public pour pouvoir être appelée directement dans les tests, ou vous pouvez déplacer cette méthode dans une classe utilitaire pour la tester plus facilement.