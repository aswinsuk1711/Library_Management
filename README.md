# Library Management System

A comprehensive Java-based Library Management System demonstrating Object-Oriented Programming principles, SOLID design principles, and multiple design patterns.

## Features

### Core Features
- **Book Management**: Add, remove, update, and search books by title, author, or ISBN
- **Patron Management**: Manage library members with different membership tiers
- **Lending System**: Complete checkout and return functionality with borrowing limits
- **Inventory Tracking**: Real-time tracking of available and borrowed books

### Advanced Features
- **Multi-Branch Support**: Manage multiple library branches with independent inventories
- **Book Transfer**: Transfer books between branches
- **Reservation System**: Reserve books that are currently checked out
- **Notification System**: Automatic notifications when reserved books become available
- **Recommendation Engine**: Collaborative filtering-based book recommendations

## Design Patterns Implemented

### 1. Singleton Pattern
- **Class**: `LibraryService`
- **Purpose**: Ensures single instance of the library system throughout the application

### 2. Factory Pattern
- **Class**: `PatronFactory`
- **Purpose**: Creates different types of patrons (Regular, Premium) with varying borrowing limits

### 3. Strategy Pattern
- **Classes**: `SearchStrategy`, `TitleSearchStrategy`, `AuthorSearchStrategy`, `ISBNSearchStrategy`
- **Purpose**: Flexible search algorithms that can be switched at runtime

### 4. Observer Pattern
- **Interface**: `BookAvailabilityObserver`
- **Purpose**: Notifies interested parties when reserved books become available

## SOLID Principles

### Single Responsibility Principle (SRP)
- Each class has one clear responsibility:
  - `Book`: Represents book data
  - `Patron`: Represents patron data
  - `LibraryService`: Manages library operations
  - `RecommendationService`: Handles recommendations

### Open/Closed Principle (OCP)
- `SearchStrategy` interface allows new search types without modifying existing code
- `Patron` abstract class allows new patron types through extension

### Liskov Substitution Principle (LSP)
- `RegularPatron` and `PremiumPatron` can substitute `Patron` without breaking functionality

### Interface Segregation Principle (ISP)
- Small, focused interfaces like `BookAvailabilityObserver` and `SearchStrategy`

### Dependency Inversion Principle (DIP)
- `LibraryService` depends on abstractions (`SearchStrategy`, `BookAvailabilityObserver`) not concrete implementations

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          LibraryService (Singleton)                      │
├─────────────────────────────────────────────────────────────────────────┤
│ - books: Map<String, Book>                                              │
│ - patrons: Map<String, Patron>                                          │
│ - branches: Map<String, LibraryBranch>                                  │
│ - loans: List<Loan>                                                     │
│ - reservations: Map<String, Queue<Reservation>>                         │
│ - observers: List<BookAvailabilityObserver>                             │
├─────────────────────────────────────────────────────────────────────────┤
│ + getInstance(): LibraryService                                         │
│ + addBook(Book): void                                                   │
│ + searchBooks(SearchStrategy, String): List<Book>                       │
│ + checkoutBook(String, String, String): void                            │
│ + returnBook(String, String, String): void                              │
│ + reserveBook(String, String, String): void                             │
│ + transferBook(String, String, String, int): boolean                    │
│ + getRecommendations(String): List<Book>                                │
└─────────────────────────────────────────────────────────────────────────┘
                    │                    │                    │
                    │                    │                    │
        ┌───────────┘                    │                    └───────────┐
        │                                │                                │
        ▼                                ▼                                ▼
┌──────────────┐              ┌──────────────────┐            ┌──────────────────┐
│     Book     │              │     Patron       │            │  LibraryBranch   │
├──────────────┤              │   (abstract)     │            ├──────────────────┤
│ - isbn       │              ├──────────────────┤            │ - branchId       │
│ - title      │              │ - patronId       │            │ - name           │
│ - author     │              │ - name           │            │ - inventory      │
│ - year       │              │ - email          │            ├──────────────────┤
├──────────────┤              │ - history        │            │ + addBook()      │
│ + getters()  │              ├──────────────────┤            │ + removeBook()   │
│ + setters()  │              │ + addLoan()      │            │ + getQuantity()  │
└──────────────┘              │ + getMax...()    │            └──────────────────┘
                              └──────────────────┘
                                      △
                                      │
                        ┌─────────────┴─────────────┐
                        │                           │
                        │                           │
              ┌─────────────────┐         ┌─────────────────┐
              │ RegularPatron   │         │ PremiumPatron   │
              ├─────────────────┤         ├─────────────────┤
              │ + getMax...(): 3│         │ + getMax...():10│
              └─────────────────┘         └─────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│                         Design Patterns                                   │
└──────────────────────────────────────────────────────────────────────────┘

┌─────────────────────┐          ┌──────────────────────────────────────┐
│  PatronFactory      │          │      SearchStrategy (interface)      │
│    (Factory)        │          ├──────────────────────────────────────┤
├─────────────────────┤          │ + search(Collection, String): List   │
│ + createPatron()    │          └──────────────────────────────────────┘
└─────────────────────┘                          △
                                                 │
                                    ┌────────────┼────────────┐
                                    │            │            │
                        ┌───────────────┐ ┌─────────────┐ ┌──────────────┐
                        │TitleSearch... │ │AuthorSear...│ │ISBNSearch... │
                        └───────────────┘ └─────────────┘ └──────────────┘

┌────────────────────────────────────────┐
│ BookAvailabilityObserver (interface)   │
│           (Observer Pattern)           │
├────────────────────────────────────────┤
│ + onBookAvailable(Book, String): void  │
└────────────────────────────────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│     Loan     │         │ Reservation  │         │    Logger    │
├──────────────┤         ├──────────────┤         ├──────────────┤
│ - book       │         │ - book       │         │ + info()     │
│ - patron     │         │ - patron     │         │ + error()    │
│ - branchId   │         │ - branchId   │         │ + warn()     │
│ - dates      │         │ - date       │         └──────────────┘
└──────────────┘         └──────────────┘
```

## Project Structure

```
src/
├── Main.java                          # Application entry point
├── model/                             # Domain models
│   ├── Book.java
│   ├── Patron.java (abstract)
│   ├── RegularPatron.java
│   ├── PremiumPatron.java
│   ├── Loan.java
│   ├── Reservation.java
│   └── LibraryBranch.java
├── service/                           # Business logic
│   ├── LibraryService.java (Singleton)
│   └── RecommendationService.java
├── pattern/                           # Design patterns
│   ├── PatronFactory.java (Factory)
│   ├── SearchStrategy.java (Strategy)
│   ├── TitleSearchStrategy.java
│   ├── AuthorSearchStrategy.java
│   ├── ISBNSearchStrategy.java
│   └── BookAvailabilityObserver.java (Observer)
├── exception/                         # Custom exceptions
│   ├── BookNotFoundException.java
│   ├── BookUnavailableException.java
│   └── PatronLimitExceededException.java
└── util/                              # Utilities
    └── Logger.java
```

## How to Run

### Prerequisites
- Java 8 or higher
- No external dependencies required

### Compilation
```bash
cd src
javac Main.java
```

### Execution
```bash
java Main
```

## Usage Examples

### Creating Patrons
```java
// Using Factory Pattern
Patron regular = PatronFactory.createPatron(
    PatronFactory.PatronType.REGULAR, "P001", "John Doe", "john@email.com");
Patron premium = PatronFactory.createPatron(
    PatronFactory.PatronType.PREMIUM, "P002", "Jane Smith", "jane@email.com");
```

### Searching Books
```java
// Using Strategy Pattern
List<Book> results = library.searchBooks(new TitleSearchStrategy(), "Java");
List<Book> byAuthor = library.searchBooks(new AuthorSearchStrategy(), "Bloch");
List<Book> byISBN = library.searchBooks(new ISBNSearchStrategy(), "978-0134685991");
```

### Checkout and Return
```java
library.checkoutBook("P001", "978-0134685991", "B001");
library.returnBook("P001", "978-0134685991", "B001");
```

### Reservations
```java
library.reserveBook("P001", "978-0134685991", "B001");
// Patron will be notified when book becomes available
```

### Recommendations
```java
List<Book> recommended = library.getRecommendations("P001");
```

## Key OOP Concepts Demonstrated

### Encapsulation
- Private fields with public getters/setters
- Immutable ISBN in Book class
- Protected internal state in all classes

### Inheritance
- `Patron` abstract class with `RegularPatron` and `PremiumPatron` subclasses
- Demonstrates polymorphic behavior with different borrowing limits

### Polymorphism
- `SearchStrategy` interface with multiple implementations
- `Patron` types can be used interchangeably
- Observer pattern allows multiple notification handlers

### Abstraction
- Abstract `Patron` class defines contract for all patron types
- Interfaces (`SearchStrategy`, `BookAvailabilityObserver`) hide implementation details

## Exception Handling
- Custom exceptions for domain-specific errors
- Proper error propagation and logging
- Graceful handling of edge cases

## Logging
- Simple custom logger for tracking system events
- Logs all major operations (add, remove, checkout, return, transfer)
- Error and warning levels for different severity

## Future Enhancements
- Persistent storage (database integration)
- Fine calculation for overdue books
- User authentication and authorization
- REST API for web/mobile clients
- Email notifications for reservations
- Advanced analytics and reporting

## Author
Library Management System - Java OOP Demonstration

## License
This project is for educational purposes.
