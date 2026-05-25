package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SampleController {
    @FXML
    Label lblTitolo;

    @FXML
    TextArea txtArea, txtAreaUtenti, txtNumeroUtenti;

    @FXML
    TextField txtMessaggio, txtIP;

    @FXML
    Button btnManda, btnConnetti, btnDisconnetti;

    private Socket socket;
    private PrintWriter clientWriter;
    private boolean connesso = false; // Aggiunta della variabile per controllare la connessione
   
    @FXML
    public void connessione(ActionEvent e) {
        try {
            String nome = txtMessaggio.getText();
            // Se il nome o l'IP sono vuoti, non connette il client
            // Aggiunta del controllo per impedire la connessione duplicata
            if (!nome.isEmpty() && !txtIP.getText().isEmpty() && !connesso) { 
            	txtArea.clear();
            	txtAreaUtenti.clear();
            	txtNumeroUtenti.clear();
            	// Creazione del socket per la connessione al server
            	socket = new Socket(txtIP.getText(), 1234);
            	btnConnetti.setDisable(true);
            	btnDisconnetti.setDisable(false);
            	
            	// Creazione del PrintWriter per l'invio dei messaggi al server
            	clientWriter = new PrintWriter(socket.getOutputStream(), true);
            	
            	
            	// Thread per la lettura dei messaggi dal server
            	new Thread(this::riceviMessaggiServer).start();
            	
            	clientWriter.println(nome);
            	txtIP.setEditable(false);
            	txtMessaggio.clear();
            	
            	connesso = true; // Impostazione della variabile di connessione a true
            }

        } catch (Exception a) {
        	txtArea.appendText("Errore durante la connessione al server: " + a.getMessage() + "\n");
        }
    }

    
    @FXML
    public void disconnessione(ActionEvent e) {
        try {
            if (socket != null && !socket.isClosed() && connesso) { // Aggiunta del controllo per disconnettersi solo se connessi
            	socket.close();
                btnConnetti.setDisable(false);
                btnDisconnetti.setDisable(true);
                txtArea.appendText("Ti sei disconnesso.\n");

                // Notifica agli altri client della disconnessione
                clientWriter.println("Client disconnesso");
                
                connesso = false; // Impostazione della variabile di connessione a false
            }
        } catch (Exception a) {
        	txtArea.appendText("Errore durante la disconnessione dal server: " + a.getMessage() + "\n");
        }
    }

    
    @FXML
    public void manda(ActionEvent e) {
        String messaggio = txtMessaggio.getText();
        clientWriter.println(messaggio);
        txtMessaggio.clear();
    }

    
    private void riceviMessaggiServer() {
        try {
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String messaggio;
            while ((messaggio = serverReader.readLine()) != null) {
                if (messaggio.startsWith("Utenti")) {
                	txtAreaUtenti.clear();
                	String[] inizio = messaggio.split(":");
                	String[] utenti = messaggio.split(":")[1].split(",");
                	txtAreaUtenti.appendText(inizio[0] + ":\n");
                	for(int i=0; i<utenti.length; i++) {
                		txtAreaUtenti.appendText(utenti[i] + "\n");
                	}
                }else if(messaggio.startsWith("N°")) {
                		txtNumeroUtenti.setText(messaggio);
                	}else {
                		txtArea.appendText(messaggio + "\n");
                }
            }
        } catch (Exception e) {}
    }

}
