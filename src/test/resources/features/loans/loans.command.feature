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
      
    Scenario: Creazione di una richiesta di prestito per un libro non disponibile
      Given una copia del libro "9788804336327" è in stato borrowed
      When l'utente crea una richiesta di prestito con i seguenti dati:
        """
        {
          "isbn": "9788804336327",
          "userId": "${USER_ID}",
          "startDate": "2026-02-23"
        }
        """
      Then la risposta ha status code 422
       And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "BOOK_NOT_AVAILABLE" |
      | type    | "BOOK_NOT_AVAILABLE" |
      
  Rule: Conferma della prenotazione di un prestito
  
    Scenario: Conferma di una richiesta di prestito in stato pending
      Given esiste un prestito per il libro ISBN "9788804336327" in attesa di conferma
      When l'amministratore conferma la richiesta del prestito
      Then la risposta ha status code 204
      And è stato generato l'evento "LoanConfirmRequested" con aggregateId "${LOAN_ID}" e payload:
        | id     | ${LOAN_ID}      |
        | isbn   | "9788804336327" |
        | userId | ${USER_ID}      |
        
    Scenario: Conferma di una richiesta di prestito non esistente
      Given il prestito con ID "100" non esiste
      When l'amministratore conferma la richiesta del prestito
      Then la risposta ha status code 422
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "LOAN_NOT_CREATED"           |
      | type    | "AGGREGATE_INVARIANT_FAILED" |
      
    Scenario: Conferma di una richiesta di prestito non in stato pending
      Given esiste un prestito per il libro ISBN "9788804336327" in attesa di conferma
      And il prestito del libro "9788804336327" è stato annullato
      When l'amministratore conferma la richiesta del prestito
      Then la risposta ha status code 422
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "INVALID_STATE_TRANSATION"   |
      | type    | "AGGREGATE_INVARIANT_FAILED" |
      
  Rule: Annullo di una richiesta di prestito
  
    Scenario: Annullo di una richiesta di prestito in stato pending
      Given esiste un prestito per il libro ISBN "9788804336327" in attesa di conferma
      When l'amministratore annulla la richiesta del prestito
      Then la risposta ha status code 204
      And è stato generato l'evento "LoanCanceled" con aggregateId "${LOAN_ID}" e payload:
      | id     | ${LOAN_ID}      |
      | isbn   | "9788804336327" |
      | userId | ${USER_ID}      |
        
    Scenario: Annullo di una richiesta di prestito non in stato pending
      Given esiste un prestito per il libro ISBN "9788804336327" in attesa di conferma
      And il prestito del libro "9788804336327" è stato confermato
      When l'amministratore annulla la richiesta del prestito
      Then la risposta ha status code 422
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "INVALID_STATE_TRANSATION"   |
      | type    | "AGGREGATE_INVARIANT_FAILED" |
      
    Scenario: Annullo di una richiesta di prestito inesistente
      Given il prestito con ID "100" non esiste
      When l'amministratore annulla la richiesta del prestito
      Then la risposta ha status code 422
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "LOAN_NOT_CREATED"           |
      | type    | "AGGREGATE_INVARIANT_FAILED" |
      
  Rule: Restituzione di un libro prestato
  
    Scenario: Registrazione del reso di un prestito confermato
      Given esiste un prestito per il libro ISBN "9788804336327" in attesa di conferma
      And il prestito del libro "9788804336327" è stato confermato
      When l'amministratore esegue l'operazione di reso del prestito
      Then la risposta ha status code 204
      And è stato generato l'evento "LoanReturned" con aggregateId "${LOAN_ID}" e payload:
      | id     | ${LOAN_ID}      |
      | isbn   | "9788804336327" |
      | userId | ${USER_ID}      |
      
    Scenario: Conferma restituzione di un libro prestato, con richiesta in stato non "confirmed"
      Given esiste un prestito per il libro ISBN "9788804336327" in attesa di conferma
      When l'amministratore esegue l'operazione di reso del prestito
      Then la risposta ha status code 422
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "INVALID_STATE_TRANSATION"   |
      | type    | "AGGREGATE_INVARIANT_FAILED" |
      
    Scenario: Conferma restituzione di un libro prestato, con richiesta di prestito non esistente
      Given il prestito con ID "100" non esiste
      When l'amministratore esegue l'operazione di reso del prestito
      Then la risposta ha status code 422
      And la risposta contiene il campo "message"
      And la risposta contiene i seguenti campi:
      | code    | "LOAN_NOT_CREATED"           |
      | type    | "AGGREGATE_INVARIANT_FAILED" |