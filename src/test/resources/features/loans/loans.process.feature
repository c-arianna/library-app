Feature: Gestione Loan Process
  Il processo di prestito coordina la richiesta di prestito e l'annullo/conferma, gestendo
  l'aggiornamento dello stato del
  libro prestato, in modo da garantire consistenza tra Loan e Book.

  Background:
     Given l'amministratore aggiunge un libro con isbn "9788804336327", autore "Italo Calvino", titolo "Il barone rampante" e descrizione
     """

     """
    And l'amministratore aggiunge 1 copie del libro "9788804336327"
    And esiste l'utente "Mario Rossi"
    
  Rule: Completamento del processo di prestito
  
    Scenario: Dopo la conferma, il prestito raggiunge lo stato finale "confirmed" e il libro è aggiornato
      Given esiste un prestito per il libro ISBN "9788804336327" in attesa di conferma
      When l'amministratore conferma la richiesta del prestito
      Then il prestito nel read model ha stato "CONFIRMED"
      And il libro "9788804336327" ha totalCopies = 1, borrowedCopies = 1, availableCopies = 0, reservedCopies = 0
      
  Rule: Fallimento del processo di prestito

    Scenario: Il processo fallisce se non è possibile completare il prestito
      Given esiste un prestito per il libro ISBN "9788804336327", senza prenotazione del libro
      When l'amministratore conferma la richiesta del prestito
      Then il prestito nel read model ha stato "FAILED"
      And il libro "9788804336327" ha totalCopies = 1, borrowedCopies = 0, availableCopies = 1, reservedCopies = 0
