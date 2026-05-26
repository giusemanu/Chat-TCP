# JavaFX TCP Chat Application

Una semplice applicazione di chat con architettura **Client-Server** basata su **socket TCP**. L'applicazione è dotata di un'interfaccia grafica (GUI) intuitiva realizzata in **JavaFX** e progettata tramite **Scene Builder**.

## Dettagli Tecnici e Architettura

### Il Server Multi-Thread
Per far sì che il server possa gestire più client contemporaneamente senza bloccare i processi di Input/Output, l'architettura implementa il multithreading. Ogni qualvolta un utente effettua una richiesta di connessione, il server accetta il socket e avvia un nuovo thread isolato che si occuperà esclusivamente della comunicazione con quel client specifico.

### Sincronizzazione dei Dati
La gestione di molteplici thread in esecuzione parallela richiede attenzione alla memoria condivisa (come ad esempio la lista dei client connessi). Il progetto fa uso di meccanismi di sincronizzazione per:
* Prevenire **race condition** quando più thread cercano di aggiungere o rimuovere client dalla lista globale nello stesso momento.
* Garantire che il broadcasting dei messaggi avvenga in sicurezza e che la comunicazione tra i blocchi non porti a corruzione di dati.

## Come iniziare

### Prerequisiti
* Java Development Kit (JDK) 11 o versioni successive
* Librerie JavaFX e relativi moduli (es. `javafx.controls`, `javafx.fxml`)
* (Opzionale) Scene Builder per la modifica visiva della GUI

### Istruzioni di Avvio
1.  **GUI:** Copia la cartella `Back` nella root.
2.  **Server:** Avvia per primo il file Main del server per metterlo in attesa di connessioni sulla porta TCP designata.
3.  **Client:** Avvia una o più istanze dell'applicazione Client. Inserisci il tuo username, l'indirizzo IP del server (es. `localhost` o `127.0.0.1`) e la porta.
