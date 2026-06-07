# Library Management System

A Library Management System built in Java using clean OOP design and SOLID principles.

## Features

- Add, search, and manage books by title, author, or ISBN
- Member registration and borrowing history
- Borrow and return books with due date tracking
- Fine calculation for overdue returns

## Design Principles

- **SOLID** principles throughout
- **Observer pattern** — notifications for due dates
- **Strategy pattern** — different fine calculation policies
- **Factory pattern** — book and member creation

## Project Structure

```
src/
├── models/        # Book, Member, BorrowRecord
├── services/      # LibraryService, FineService
├── patterns/      # Observer, Strategy implementations
└── Main.java
```

## Run

```bash
javac -d out src/**/*.java
java -cp out Main
```

## Tech

Java · OOP · Design Patterns

