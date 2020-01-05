package common;
import java.io.Serializable;

public class GameState implements Serializable {

    public final int points;
    public final int lives;
    public final String currentGuessState;


    public GameState(int points, String word, int lives) {
        this.points = points;
        this.lives = word.length();
        this.currentGuessState = word;
    }

    @Override
    public String toString() {
        return "{\"points\":" + String.valueOf(points) + ", \"word\":\"" + currentGuessState + ", \"lives\":" + String.valueOf(lives) + '}';
    }

    public static GameState fromString(String string) {
        if (string.startsWith("{\"points\":")) {
            int endIndex = string.indexOf(", ");
            String sub = string.substring(10, endIndex);
            int points = Integer.parseInt(sub);
            int wordEnd = string.indexOf("\", ", endIndex + 10);
            String word = string.substring(endIndex + 10, wordEnd);
            sub = string.substring(wordEnd + 11, string.length() - 1);
            int lives = Integer.parseInt(sub);
            return new GameState(points, word, lives);
        }
    return null;

    }
}
