# Library Application

Java / Spring Boot implementation

## Overview

This repository represents the second iteration of a Library Management System developed as part of a software architecture mentoring journey.

After the first implementation in Node.js and TypeScript, an important lesson emerged: learning a new ecosystem while simultaneously exploring advanced architectural patterns often divides attention between framework-specific concerns and architectural thinking.

For this reason, the project was reimplemented using Java and Spring Boot, technologies I was already familiar with.

The primary objective of this phase was to focus more directly on:

- Domain Driven Design
- Event Sourcing
- Event-driven workflows
- CQRS-inspired read models
- Domain modelling
- Software architecture

The application manages a library catalogue and the lifecycle of book loans through a REST API.

Although fully functional, the project should primarily be viewed as an architectural learning milestone that documents the evolution of the ideas explored during the mentoring journey.

Compared to the previous Node.js implementation, this version introduces a dedicated event store, aggregate reconstruction through event replay, CQRS-style read models, process coordination through a reactor component and a JPA-based persistence layer.

The objective was not to rebuild the same application in another technology stack, but to validate architectural decisions in a more mature implementation.

## Mentoring Journey

This project was developed with the guidance of a senior software architect as part of a structured mentoring program focused on backend architecture and domain modelling.

Architectural decisions and learning objectives were regularly discussed and reviewed with my mentor, whose guidance strongly influenced the structure and evolution of the project.

Compared to the previous Node.js implementation, this version places greater emphasis on:

- architectural consistency;
- separation of concerns;
- aggregate design;
- event modelling;
- persistence boundaries;
- testing strategy.

The goal was to apply the lessons learned from the first iteration and refine the overall architecture.

## Learning Journey

This repository represents the second phase of a multi-stage evolution of the same business domain.

| Phase | Technology | Main Focus |
|---------|---------|---------|
| Phase 1 | Node.js + TypeScript | Learning a new ecosystem while exploring DDD and Event Sourcing |
| Phase 2 | Java + Spring Boot Monolith | Consolidating architectural concepts in a familiar ecosystem |
| Phase 3 | Java + Spring Boot Microservices | Applying the lessons learned to a distributed architecture |
| Phase 4 | Angular Frontend | Demonstrating the platform through a web interface |

The subsequent repositories should be considered natural evolutions of the architectural concepts introduced in this implementation.

## Architecture Concepts

The project explores several backend architecture patterns:

- Domain Driven Design (DDD)
- Event Sourcing
- CQRS-inspired read models
- Aggregate-based domain modelling
- Event Store
- Domain Events
- Reactor workflows
- Repository Pattern

The system is organized into three main layers:

```text
Domain
├── Models
├── Value Objects
├── Domain Events
└── Domain Errors

Application
├── Services
├── Aggregates
├── Reactors
├── Projectors
└── Repositories

Infrastructure
├── REST Controllers
├── JPA Repositories
├── Event Store
├── Persistence Adapters
└── Exception Handling
```

## Core Domain

The application models the operations of a small library.

The domain revolves around two primary concepts:

- books and their availability within the catalogue;
- loan requests and their lifecycle.

Business rules are enforced through aggregates, domain events and state transitions rather than relying solely on database constraints.

### Book Management

- Register books
- Add copies
- Remove copies
- Search catalogue

### Loan Management

- Request loans
- Confirm loans
- Cancel loans
- Return books
- Search loans

## Event Sourcing Approach

The system uses Event Sourcing as its persistence model.

Rather than storing only the current state of an entity, domain events are persisted and aggregates are rebuilt by replaying the corresponding event stream.

Examples of events include:

- BookRegistered
- BookCopiesAdded
- BookReserved
- BookBorrowed
- BookReturned
- LoanRequested
- LoanReserved
- LoanConfirmed
- LoanReturned

The event stream is subsequently used to:

- rebuild aggregate state;
- update read models;
- coordinate business processes.

Read models are maintained through projections and projectors in order to support efficient query operations.

## Testing

The project includes:

- Behavior-Driven Development (BDD) tests using Cucumber and Gherkin
- Spring Boot integration tests
- MySQL Testcontainers environments
- End-to-end business process validation

Business rules, aggregate behavior, domain events and event-driven workflows are validated through executable specifications.

## Running the Tests

The project includes a Behavior-Driven Development (BDD) test suite written in Gherkin.

The business scenarios validate the complete application stack, including aggregates, event persistence, projections and process coordination.

### Requirements

Integration tests rely on:

- Docker
- Testcontainers

Docker must be installed and running before executing the test suite.

### Execute the Test Suite

```bash
mvn test
```

During execution, Testcontainers automatically provisions and disposes of a temporary MySQL instance.

No manually configured database is required for test execution.

## Lessons Learned

This implementation confirmed that Event Sourcing, CQRS-style projections and aggregate-based domain modelling can provide a clear separation between business behavior and persistence concerns.

At the same time, several new challenges became apparent:

- event publication reliability;
- transactional consistency;
- scalability of synchronous event dispatching;
- service boundaries;
- operational complexity.

These topics motivated the transition towards a microservices-based architecture in the next phase of the mentoring journey.

## Why This Repository Exists

The purpose of this repository is not to showcase a perfect implementation.

Its purpose is to document the learning process, architectural experiments and technical decisions that led to more mature implementations of the same domain.

I believe there is value not only in presenting final solutions, but also in showing the evolution of ideas and the lessons learned along the way.

## Running the Project

The application requires:

- Java 21
- Maven
- MySQL 8+

The default configuration expects a local MySQL instance running on:

```text
localhost:3306
```

### Database Setup

Database initialization scripts are available under:

```text
src/main/resources/db/sql/
├── 01_createDBSchema.sql
└── 02_initTables.sql
```

- `01_createDBSchema.sql` creates the database schema and application user.
- `02_initTables.sql` creates the event store and read model tables.

Execute the SQL scripts in the following order:

1. `01_createDBSchema.sql`
2. `02_initTables.sql`

### Build the Application

```bash
mvn clean package
```

### Start the Application

```bash
mvn spring-boot:run
```

Alternatively:

```bash
java -jar target/libraryApp-0.0.1-SNAPSHOT.jar
```

after building the project.

The repository includes default local development credentials exclusively to simplify project setup and onboarding.

These credentials are intended only for local development and demonstration purposes and do not provide access to any external systems.

## License

This repository is published for educational and portfolio purposes.

All rights reserved by the author unless explicitly stated otherwise.