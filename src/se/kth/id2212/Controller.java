package se.kth.id2212;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Controller {

    public Button connectBtn;
    public TextField port;
    public TextField host;
    public Label statusLabel;
    public Label wordLabel;
    public TextField guessTxt;
    public Button guessBtn;
    private Socket clientSocket;
    private String wordToGuess;
    private int guessesRemaining;

    public void connectHandle(ActionEvent event) throws IOException {
        connectBtn.setDisable(true);
        port.setDisable(true);
        host.setDisable(true);
        guessTxt.setDisable(false);
        guessBtn.setDisable(false);

        try
        {
            clientSocket = new Socket(host.getText(), Integer.parseInt(port.getText()));
            statusLabel.setText("Connected to host: " + host.getText() + ", begin guessing below.");
        } catch (UnknownHostException e)
        {
            statusLabel.setText("Don't know about host: " + host.getText() + ".");
            reset();
        } catch (IOException e)
        {
            statusLabel.setText("Couldn't get I/O for the connection to: " + host.getText() + "");
            reset();
        }

        BufferedInputStream in = null;

        try
        {
            in = new BufferedInputStream(clientSocket.getInputStream());
        } catch (IOException e)
        {
            statusLabel.setText(e.getMessage());
            reset();
        }

        byte[] msg = new byte[4096];
        int bytesRead = 0;
        int n;

        while ((n = in.read(msg, bytesRead, 256)) != -1)
        {
            bytesRead += n;
            if (bytesRead == 4096)
            {
                break;
            }
            if (in.available() == 0)
            {
                break;
            }
        }
        wordToGuess = new String(msg);
        wordToGuess = wordToGuess.substring(0, bytesRead);
        System.out.println(wordToGuess);
        wordLabel.setText(new String(new char[wordToGuess.length()]).replace("\0", "-"));
        guessesRemaining = 10;
    }

    private void reset () {
        connectBtn.setDisable(false);
        port.setDisable(false);
        host.setDisable(false);
        guessTxt.setText("");
        wordLabel.setText("");
        guessTxt.setDisable(true);
        guessBtn.setDisable(true);
        guessesRemaining = 0;
    }

    public void guessHandle(ActionEvent event) {
        if (!wordToGuess.contains(guessTxt.getText()) && --guessesRemaining == 0)
        {
            handleLoss();
            return;
        }
        if (guessTxt.getText().length() == 1)
        {
            char[] temp = new char[wordToGuess.length()];
            for (int i = 0; i < wordToGuess.length(); i ++)
            {
                if (wordToGuess.charAt(i) == guessTxt.getText().charAt(0))
                    temp[i] = wordToGuess.charAt(i);
                else if (wordLabel.getText().charAt(i) != '-')
                    temp[i] = wordToGuess.charAt(i);
                else
                    temp[i] = '-';
            }
            wordLabel.setText(new String(temp));
        }
        if (guessTxt.getText().equals(wordToGuess) || wordLabel.getText().equals(wordToGuess))
            handleWin();
        else
            statusLabel.setText("Guesses remaining: " + guessesRemaining);
        guessTxt.setText("");
    }

    private void handleWin() {
        reset();
        wordLabel.setText(wordToGuess);
        statusLabel.setText("You win!");
    }

    private void handleLoss() {
        reset();
        wordLabel.setText(wordToGuess);
        statusLabel.setText("You lost, try again.");
    }
}
