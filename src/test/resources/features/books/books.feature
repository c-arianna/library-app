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
      Then la risposta ha status code 200
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
      Given aggiungo un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
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