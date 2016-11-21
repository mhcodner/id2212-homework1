package se.kth.id2212;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SimpleConnectionHandler extends Thread
{
    private Socket clientSocket;

    public SimpleConnectionHandler(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
        BufferedInputStream in;
        BufferedOutputStream out;

        try
        {
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = new BufferedOutputStream(clientSocket.getOutputStream());
        } catch (IOException e)
        {
            System.out.println(e.toString());
            return;
        }

        List<String> list = new ArrayList<>();
        String chosenWord = "";

        try (Scanner scanner = new Scanner(new File("words.txt"))) {
            while (scanner.hasNext()) {
                list.add(scanner.nextLine().toLowerCase());
            }
            Random r = new Random();
            chosenWord = list.get(r.nextInt(list.size()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try
        {
            System.out.println(chosenWord);

            out.write(chosenWord.getBytes());

            out.flush();

        } catch (IOException e)
        {
            System.out.println(e.toString());
        }

        try
        {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}