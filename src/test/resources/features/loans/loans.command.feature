Feature: Gestione dei prestiti dei libri tramite l'applicazione
  L'amministratore della bibloteca può
  - confermare una richiesta di prestito
  - rifiutare una richiesta di prestito
  - eseguire la restituzione di un libro

  Gli utenti possono
  - inserire una richiesta di prestito per un libro

  Background:
    Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
     """

     """
    And l'amministratore aggiunge 1 copie del libro "9788804336327"
    And esiste l'utente "Mario Rossi"
    
  Rule: Creazione di una richiesta di prestito

    Scenario: Creazione di una richiesta di prestito con successo
      When l'utente crea una richiesta di prestito con i seguenti dati:
        """
        {
          "isbn": "9788804336327",
          "userId": "${USER_ID}",
          "startDate": "2026-02-23"
        }
        """
      Then la risposta ha status code 200
      And è stato generato l'evento "LoanRequested" con aggregateId "${LOAN_ID}" e payload:
        | isbn   | "9788804336327"  |
        | userId | ${USER_ID}       |
        
    Scenario: Creazione di una richiesta di prestito per un libro non presente
      Given il catalogo non contiene il libro con isbn "9788804776369"
      When l'utente crea una richiesta di prestito con i seguenti dati:
        """
        {
          "isbn": "9788804776369",
          "userId": "1",
          "startDate": "2026-02-23"
        }
        """
      Then la risposta ha status code 404
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "BOOK_NOT_FOUND"     |
      | type    | "RESOURCE_NOT_FOUND" |
      
    Scenario: Creazione di una richiesta di prestito per un utente non esistente
      Given l'utente con ID "1" non esiste
      When l'utente crea una richiesta di prestito con i seguenti dati:
        """
        {
          "isbn": "9788804336327",
          "userId": "1",
          "startDate": "2026-02-23"
        }
        """
      Then la risposta ha status code 404
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "USER_NOT_FOUND"     |
      | type    | "RESOURCE_NOT_FOUND" |
      
    Scenario: Creazione di una richiesta di prestito con dati non validi
      When l'utente crea una richiesta di prestito con i seguenti dati:
        """
        {
          "startDate": "2026-02-23"
        }
        """
      Then la risposta ha status code 400
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "VALIDATION_ERROR" |
      | type    | "VALIDATION_ERROR" |