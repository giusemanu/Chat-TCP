package application;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class SampleController {
	@FXML
	Label lblTitolo;
	
    @FXML
    TextArea txtArea, txtAreaUtenti, txtNumeroUtenti;

    @FXML
    Button btnSend, btnAvvia, btnArresta;

    private ServerSocket serverSocket;
    private ArrayList<PrintWriter> utenti = new ArrayList<PrintWriter>();
    private ArrayList<String> nomiUtenti = new ArrayList<String>();

    @FXML
    private void avvia(ActionEvent event) {
        try {
            serverSocket = new ServerSocket(1234);
            txtArea.appendText("Server avviato!\n");
            btnAvvia.setDisable(true);
            btnArresta.setDisable(false);
            
            // Avvio del thread per accettare le connessioni dei client
            new Thread(this::accettaConnessioni).start();
        } catch (IOException e) {
        	txtArea.appendText("Errore nell'avvio del server: " + e.getMessage() + "\n");
        }
    }

    @FXML
    private void arresta(ActionEvent event) {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                txtArea.appendText("Server arrestato!\n");
                btnAvvia.setDisable(false);
                btnArresta.setDisable(true);
            } catch (IOException e) {
            }
        }
    }


    private void accettaConnessioni() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter client = new PrintWriter(clientSocket.getOutputStream(), true);
                utenti.add(client);

                // Leggi il nome del client connesso
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String nome = in.readLine();

                // Notifica agli altri client della connessione
                broadcast("<" + nome + "> : " + "si è connesso.", client);
                txtArea.appendText(nome + " si è connesso.\n");
                
                //Aggiornamento lista utenti
                txtAreaUtenti.clear();
                nomiUtenti.add(nome);
                listaUtenti();
                numeroUtenti();
                
                // Invia il messaggio di benvenuto al client connesso
                client.println("Benvenuto, " + nome + "!");

                // Avvio del thread per la gestione della chat del client
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                    	gestoreConnessioneClient(clientSocket, client, nome);
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
        	
        }
    }

    private void gestoreConnessioneClient(Socket clientSocket, PrintWriter client, String nome) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String messaggio;
            while ((messaggio = in.readLine()) != null) {
                broadcast("<" + nome + "> : " + messaggio, client);
                client.println("<" + nome + "> : " + messaggio);
            }

            //Notifica agli altri client della disconnessione
            broadcast("<" + nome + "> : " + "si è disconnesso.", client);
            txtArea.appendText(nome + " si è disconnesso\n");
            
            //Aggiornamento lista utenti
            txtAreaUtenti.clear();
            nomiUtenti.remove(nome);
            listaUtenti();
            numeroUtenti();
            
            in.close();
            client.close();
            utenti.remove(client);
        } catch (IOException e) {
            txtArea.appendText("Errore nella gestione della connessione: " + e.getMessage());
        }
    }


    private void broadcast(String messaggio, PrintWriter io) {
        for (PrintWriter p : utenti) {
        	if(p!=io)
        		p.println(messaggio);
        }

        // Ottieni la data e l'ora correnti
        LocalDateTime dataOdierna = LocalDateTime.now();

        // Definizione del formato di output desiderato
        DateTimeFormatter formattazione = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Formatta la data e l'ora correnti come stringa utilizzando il formato specificato
        String data = dataOdierna.format(formattazione);

        // Scrittura del messaggio nel file di log
        try {
            FileWriter fw = new FileWriter("C:\\Back\\messaggi.log", true);
            fw.write(data + "\t|\t" + messaggio + "\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del messaggio nel file di log: " + e.getMessage());
        }
    }
    
    private String listaUtenti() {
    	String s="";
    	s = s + "Utenti connessi:";
    	txtAreaUtenti.appendText("Utenti connessi:\n");
    	for (String utente : nomiUtenti) {
        	txtAreaUtenti.appendText(utente + "\n");
            s = s + utente + ",";
        }
    	for (PrintWriter p : utenti) {
        		p.println(s);
        }
    	return s;
    }
    
    private String numeroUtenti() {
    	String s="";
    	s = s + "N°: " +nomiUtenti.size();
    	txtNumeroUtenti.setText(s);
    	for (PrintWriter p : utenti) {
    		p.println(s);
    	}
    	return s;
    }
}