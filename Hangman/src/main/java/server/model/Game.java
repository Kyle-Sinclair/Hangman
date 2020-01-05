package server.model;

import common.GameState;
import common.Message;
import common.MessageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Game {


    private static final ArrayList<String> DICTIONARY = new ArrayList();
    private static final Random RNG = new Random();


    private String currentState, wordToGuess;
    private int score, lives;

    private final boolean wrong_guesses[] = new boolean[26];
    private final boolean repeat_guesses[] = new boolean[26];



    public static void initializeDictionary(String dictName) throws FileNotFoundException, IOException {

        File file = new File(dictName);
        if (!file.exists() || !file.canRead())
            throw new FileNotFoundException(dictName);

        FileReader fileReader = new FileReader(file);
        BufferedReader in = new BufferedReader(fileReader);

        String line;
        while ((line = in.readLine()) != null) { DICTIONARY.add(line.toUpperCase()); }

        fileReader.close();
        DICTIONARY.trimToSize();
        System.out.println("Dictionary of words loaded. Number of words: " + DICTIONARY.size());
    }

    private static String toFullForm(String item) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < item.length(); i++) {
            sb.append(item.charAt(i));
            if (i + 1 != item.length())
                sb.append(' ');
        }
        return sb.toString();
    }



    private void newRound() {
        if (wordToGuess != null)
            if (wordToGuess.compareTo(currentState) == 0)
                score++;
            else
                score--;

        wordToGuess = DICTIONARY.get(RNG.nextInt(DICTIONARY.size()));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordToGuess.length(); i++)
            sb.append('_');
        currentState = sb.toString();
        lives = wordToGuess.length();
        for (int i = 0; i < wrong_guesses.length; i++)
            wrong_guesses[i] = false;
    }

    private GameState getSnapshot(){
        return new GameState(score, toFullForm(currentState), lives);
    }


    public Message makeAGuess(String guess){

        if (!isALegalGuess(guess))
            return buildServerInterfaceResponse(MessageType.ILLEGAL_RESPONSE);

        if (isSingleLetter(guess)) {
            char g = guess.charAt(0);
            if(wrong_guesses[g - 'A']){
                return buildServerInterfaceResponse(MessageType.INCORRECT_RESPONSE);
            }

            char g = guess.charAt(0);
            if (wordToGuess.contains(guess)) {
                // Check for a repeat guess


                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < wordToGuess.length(); i++) {
                    if (wordToGuess.charAt(i) == g)
                        sb.append(g);
                    else
                        sb.append(this.currentState.charAt(i));
                }

                this.currentState = sb.toString();

                if (!isInValidState()) {
                    newRound();
                    return buildServerInterfaceResponse(MessageType.VICTORY_RESPONSE);
                }
                return buildServerInterfaceResponse(MessageType.CORRECT_RESPONSE);
            }

            else {
                    // Check for a repeat guess
                    if (wrong_guesses[g - 'A'])
                        return buildServerInterfaceResponse (MessageType.REPEAT_RESPONSE);
                    // Reduce lives counter!
                    wrong_guesses[g - 'A'] = true;

                    if (--lives <= 0) { newRound(); }


                return buildServerInterfaceResponse (MessageType.INCORRECT_RESPONSE);
                }

            } else {
                if (wordToGuess.compareTo(guess) == 0) {
                    this.currentState = guess; // this will update the points
                    newRound();
                }
                return buildServerInterfaceResponse (MessageType.ILLEGAL_RESPONSE);
            }
        }

        private void guessSingleLetter(){

        }

        private void guessWholeWord(){

        }


        /*
        * Response Functions
         */
        private Message buildServerInterfaceResponse(MessageType messageType){
            GameState state = getSnapshot();
            String currentState = state.toString();
            String textResult = "";

            switch(messageType){
                case ILLEGAL_RESPONSE:
                    textResult = "Illegal guess.";
                    break;
                case CORRECT_RESPONSE:
                    textResult = "Hit!";
                    break;
                case INCORRECT_RESPONSE:
                    textResult = "Incorrect!";
                    break;
                case VICTORY_RESPONSE:
                    textResult = "You won a round!";
                    break;
                case REPEAT_RESPONSE:
                    textResult = "You've guessed this before ";
                    break;
                case LOSS_RESPONSE:
                    textResult = "Too bad. You lose.";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value in the buildServerInterfaceResponse method" );
            }
            return new Message(messageType,textResult + " Game Status " + currentState);
        }


    /*
    * Sanity Checking Functions
     */

    private boolean isSingleLetter(String guess){
        return guess.length() == 1;
    }
    public boolean isALegalGuess(String guess) {
        return guess.length() == 1 || guess.length() == this.wordToGuess.length();
    }

    private boolean isInValidState() {
        return (wordToGuess != null && wordToGuess.compareTo(currentState) != 0 && lives > 0);
    }

    private boolean isRepeatGuess(String guess){
        return wordToGuess.contains(guess);
    }
    private boolean isIncorrectGuess(){
        return true;
    }

    private boolean isCorrectGuess(){
        return true;
    }
    private boolean isGameWon(){
        return isInValidState();
    }



    }



