package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        List<String> usernames = new ArrayList<String>();
        HashMap <String, Integer> disponibilita = new HashMap <>();
            
        disponibilita.put("Gold", 10);
        disponibilita.put("Pit", 30);
        disponibilita.put("Parterre", 60);
        ServerSocket ss = new ServerSocket(3000);
        Socket mySocket = ss.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        PrintWriter out = new PrintWriter(mySocket.getOutputStream(), true);

        out.println("WELCOME");
        String loginUsername;

        while (true) {
            loginUsername = in.readLine();
            String[] parti = loginUsername.split(" ");
            if (parti.length == 2 && parti[0].equals("LOGIN")) {

                if (!usernames.contains(loginUsername)) {
                    usernames.add(loginUsername);
                    break;
                }
                out.println("ERR USERNAME");
            }
        }

        out.println("OK");
        boolean quit = false;

        do {
            String commandoClient = in.readLine();
            if (commandoClient == null) break; 
            switch (commandoClient) {
                case "N":
                
                    out.println("AVAIL Gold: " + disponibilita.get("Gold") + " Pit: " + disponibilita.get("Pit") + " Parterre: " + disponibilita.get("Parterre"));

                    break;
                case "BUY":
                      String lineaDettagli = in.readLine();
                      if(lineaDettagli == null){
                        quit = true;
                        break;
                      }
                 
                    String[] partiBuy = lineaDettagli.trim().split(" ", 2);
                    if (partiBuy.length != 2) {
                        out.println("ERR SYNTAX");
                        break;
                    }

                    String tipo = partiBuy[0];
                    // Normalizza la prima lettera maiuscola (es. "gold" -> "Gold") per evitare errori di battitura
                    if (!tipo.isEmpty()) {
                        tipo = tipo.substring(0, 1).toUpperCase() + tipo.substring(1).toLowerCase();
                    }
                    String quantitaStr = partiBuy[1];

                    // Controlla se il tipo di biglietto esiste
                    if (!disponibilita.containsKey(tipo)) {
                        out.println("ERR USERTYPE");
                        break;
                    }

                    // Tenta di convertire la quantità in numero intero
                    try {
                        int quantitaRichiesta = Integer.parseInt(quantitaStr);
                        if (quantitaRichiesta <= 0) {
                            out.println("ERR SYNTAX");
                            break;
                        }

                        int attuali = disponibilita.get(tipo);
                        // Controlla la disponibilità residua
                        if (quantitaRichiesta <= attuali) {
                            disponibilita.put(tipo, attuali - quantitaRichiesta);
                            out.println("OK");
                        } else {
                            out.println("KO"); // Stock insufficiente
                        }
                    } catch (NumberFormatException e) {
                        out.println("ERR SYNTAX"); // Quantità inserita non valida
                    }

                    break;
                case "QUIT":
                       out.println("BYE");
                    break;

                default:
                       out.println("ERR USERNAME");
                    break;
            }
        }while ( quit == false);
        // Chiude le risorse al termine della sessione
        in.close();
        out.close();
        mySocket.close();
        ss.close();

        }

    }