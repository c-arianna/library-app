Feature: Gestione del catalogo della biblioteca tramite l'applicazione
  L'amministratore della bibloteca può
  - aggiungere nuovi libri
  - visualizzare i libri presenti nel catalogo

  Gli utenti possono
  - visualizzare i libri presenti nel catalogo

  Rule: Inserimento di un nuovo libro nel catalogo

    Scenario: Aggiunta di un nuovo libro con successo
      When l'amministratore aggiunge un libro al catalogo con i seguenti dati:
        """
        {
          "isbn": "9788804336327",
          "author": "Italo Calvino",
          "title": "Il barone rampante"
        }
        """
      Then la risposta ha status code 201
      And la risposta contiene il campo "isbn"
	  And è stato generato l'evento "BookRegistered" con aggregateId "9788804336327"
      
    Scenario: Aggiunta di un libro con richiesta non conforme al contratto API
      When l'amministratore aggiunge un libro al catalogo con i seguenti dati:
        """
        {
          "isbn": "9788804336327",
          "autore": "Italo Calvino",
          "title": "Il barone rampante"
        }
        """
      Then la risposta ha status code 400
      And la risposta contiene il campo "code"
      And la risposta contiene il campo "type"
      And la risposta contiene il campo "message"
      
    Scenario: Aggiunta di un libro con dati non validi secondo le regole di dominio
      When l'amministratore aggiunge un libro al catalogo con i seguenti dati:
        """
        {
          "isbn": "123"
        }
        """
      Then la risposta ha status code 400
      And la risposta contiene il campo "code"
      And la risposta contiene il campo "type"
      And la risposta contiene il campo "message"
      
     Scenario: Aggiunta di un libro già presente nel catalogo
      Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
        """
        Il barone rampante (1957) è il secondo libro della trilogia I nostri antenati
        """
      When l'amministratore aggiunge un libro al catalogo con i seguenti dati:
        """
        {
          "isbn": "9788804336327",
          "author": "Italo Calvino",
          "title": "Il barone rampante"
        }
        """
      Then la risposta ha status code 409
      And la risposta contiene il campo "code"
      And la risposta contiene il campo "type"
      And la risposta contiene il campo "message"
      
  Rule: Consultazione del catalogo libri

    Scenario: Consultazione del catalogo vuoto
      When l'utente visualizza il catalogo dei libri
      Then la risposta ha status code 200
      And la risposta contiene il campo "books"
      And "books" è una lista vuota
      
    Scenario: Consultazione del catalogo con libri presenti
      Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
        """
        Il barone rampante (1957) è il secondo libro della trilogia I nostri antenati
        """
      And l'amministratore aggiunge un libro con isbn "978-8804776369", autore "Italo Calvino", titolo "Il visconte dimezzato" e descrizione
        """

        """
      When l'utente visualizza il catalogo dei libri
      Then la risposta ha status code 200
      And la risposta contiene il campo "books"
      And "books" contiene 2 elementi
      And "books" ha un elemento con i campi:                                                         
        | isbn        | "9788804336327"                                                                 |
        | author      | "Italo Calvino"                                                                 |
        | title       | "Il barone rampante"                                                            |
        | description | "Il barone rampante (1957) è il secondo libro della trilogia I nostri antenati" |
      And "books" ha un elemento con i campi:
        | isbn        | "9788804776369"         |
        | author      | "Italo Calvino"         |
        | title       | "Il visconte dimezzato" |
        | description | EMPTY                   |
        
    Scenario: Consultazione del catalogo filtrata per autore non presente
      Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
        """
        Il barone rampante (1957) è il secondo libro della trilogia I nostri antenati
        """
      When l'utente visualizza il catalogo dei libri, con filtro di ricerca
        | author | "Shakespeare" |
      Then la risposta ha status code 200
      And la risposta contiene il campo "books"
      And "books" è una lista vuota
      
    Scenario: Consultazione del catalogo filtrata per autore presente
      Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
        """
        Il barone rampante (1957) è il secondo libro della trilogia I nostri antenati
        """
      When l'utente visualizza il catalogo dei libri, con filtro di ricerca
        | author | Italo Calvino |
      Then la risposta ha status code 200
      And la risposta contiene il campo "books"
      And "books" contiene 1 elementi
      And "books" ha un elemento con i campi:
        | isbn        | "9788804336327"                                                                 |
        | author      | "Italo Calvino"                                                                 |
        | title       | "Il barone rampante"                                                            |
        | description | "Il barone rampante (1957) è il secondo libro della trilogia I nostri antenati" |
        
  Rule: Gestione delle copie di un libro
  
    Scenario: aggiunta di una copia di un libro con successo
      Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
        """

        """
      When l'amministratore aggiunge una copia del libro "9788804336327", con i seguenti dati:
        """
        {
          "quantity": 1
        }
        """
      Then la risposta ha status code 204
      And è stato generato l'evento "BookCopiesAdded" con aggregateId "9788804336327"
      
    Scenario: Aggiunta di una copia di un libro con quantità negativa
      Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
        """

        """
      When l'amministratore aggiunge una copia del libro "9788804336327", con i seguenti dati:
        """
        {
          "quantity": -1
        }
        """
      Then la risposta ha status code 422
      And la risposta contiene il campo "code"
      And la risposta contiene il campo "type"
      And la risposta contiene il campo "message"
      
    Scenario: Aggiunta di una copia di un libro non presente
      When l'amministratore aggiunge una copia del libro "9788804336327", con i seguenti dati:
        """
        {
          "quantity": 1
        }
        """
      Then la risposta ha status code 422
      And la risposta contiene il campo "code"
      And la risposta contiene il campo "type"
      And la risposta contiene il campo "message"
      
    Scenario: Rimozione di 2 copie di un libro con successo
      Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
        """

        """
      And l'amministratore aggiunge 2 copie del libro "9788804336327"
      When l'amministratore rimuove copie del libro "9788804336327", con i seguenti dati:
        """
        {
          "quantity": 2,
          "reason": "Copies lost"
        }
        """
      Then la risposta ha status code 204
      And è stato generato l'evento "BookCopiesRemoved" con aggregateId "9788804336327"
      
    Scenario: Rimozione di una copia di un libro non presente
      When l'amministratore rimuove copie del libro "9788804336327", con i seguenti dati:
        """
        {
          "quantity": 2,
          "reason": "Copies lost"
        }
        """
      Then la risposta ha status code 422
      And la risposta contiene il campo "code"
      And la risposta contiene il campo "type"
      And la risposta contiene il campo "message"
      
    Scenario: Rimozione di una copia di un libro con quantità non valida
      Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
        """

        """
      When l'amministratore rimuove copie del libro "9788804336327", con i seguenti dati:
        """
        {
          "quantity": -1
        }
        """
      Then la risposta ha status code 422
      And la risposta contiene il campo "code"
      And la risposta contiene il campo "type"
      And la risposta contiene il campo "message"