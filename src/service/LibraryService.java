package service;

import model.*;
import pattern.*;
import exception.*;
import util.Logger;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {
    private static LibraryService instance;
    
    private final Map<String, Book> books;
    private final Map<String, Patron> patrons;
    private final Map<String, LibraryBranch> branches;
    private final List<Loan> loans;
    private final Map<String, Queue<Reservation>> reservations;
    private final List<BookAvailabilityObserver> observers;
    private final RecommendationService recommendationService;

    private LibraryService() {
        this.books = new HashMap<>();
        this.patrons = new HashMap<>();
        this.branches = new HashMap<>();
        this.loans = new ArrayList<>();
        this.reservations = new HashMap<>();
        this.observers = new ArrayList<>();
        this.recommendationService = new RecommendationService();
    }

    public static synchronized LibraryService getInstance() {
        if (instance == null) {
            instance = new LibraryService();
        }
        return instance;
    }

    // Book Management
    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
        Logger.info("Book added: " + book.getIsbn());
    }

    public void removeBook(String isbn) throws BookNotFoundException {
        if (!books.containsKey(isbn)) {
            throw new BookNotFoundException("Book not found: " + isbn);
        }
        books.remove(isbn);
        Logger.info("Book removed: " + isbn);
    }

    public void updateBook(String isbn, String title, String author, int year) throws BookNotFoundException {
        Book book = books.get(isbn);
        if (book == null) {
            throw new BookNotFoundException("Book not found: " + isbn);
        }
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublicationYear(year);
        Logger.info("Book updated: " + isbn);
    }

    public List<Book> searchBooks(SearchStrategy strategy, String query) {
        return strategy.search(books.values(), query);
    }

    // Patron Management
    public void addPatron(Patron patron) {
        patrons.put(patron.getPatronId(), patron);
        Logger.info("Patron added: " + patron.getPatronId());
    }

    public void updatePatron(String patronId, String name, String email) {
        Patron patron = patrons.get(patronId);
        if (patron != null) {
            patron.setName(name);
            patron.setEmail(email);
            Logger.info("Patron updated: " + patronId);
        }
    }

    public List<Loan> getPatronHistory(String patronId) {
        Patron patron = patrons.get(patronId);
        return patron != null ? patron.getBorrowingHistory() : Collections.emptyList();
    }

    // Branch Management
    public void addBranch(LibraryBranch branch) {
        branches.put(branch.getBranchId(), branch);
        Logger.info("Branch added: " + branch.getBranchId());
    }

    public void addBookToBranch(String branchId, String isbn, int quantity) {
        LibraryBranch branch = branches.get(branchId);
        if (branch != null) {
            branch.addBook(isbn, quantity);
            Logger.info(String.format("Added %d copies of %s to branch %s", quantity, isbn, branchId));
            notifyReservations(isbn, branchId);
        }
    }

    public boolean transferBook(String fromBranch, String toBranch, String isbn, int quantity) {
        LibraryBranch from = branches.get(fromBranch);
        LibraryBranch to = branches.get(toBranch);
        
        if (from != null && to != null && from.removeBook(isbn, quantity)) {
            to.addBook(isbn, quantity);
            Logger.info(String.format("Transferred %d copies of %s from %s to %s", 
                quantity, isbn, fromBranch, toBranch));
            notifyReservations(isbn, toBranch);
            return true;
        }
        return false;
    }

    // Lending Operations
    public void checkoutBook(String patronId, String isbn, String branchId) 
            throws BookNotFoundException, BookUnavailableException, PatronLimitExceededException {
        
        Patron patron = patrons.get(patronId);
        Book book = books.get(isbn);
        LibraryBranch branch = branches.get(branchId);

        if (book == null) throw new BookNotFoundException("Book not found: " + isbn);
        if (branch == null) throw new BookNotFoundException("Branch not found: " + branchId);

        long activeLoans = loans.stream()
            .filter(l -> l.getPatron().equals(patron) && !l.isReturned())
            .count();

        if (activeLoans >= patron.getMaxBooksAllowed()) {
            throw new PatronLimitExceededException("Patron has reached borrowing limit");
        }

        if (branch.getAvailableQuantity(isbn) <= 0) {
            throw new BookUnavailableException("Book not available at branch: " + branchId);
        }

        branch.removeBook(isbn, 1);
        Loan loan = new Loan(book, patron, branchId);
        loans.add(loan);
        patron.addLoan(loan);
        Logger.info(String.format("Book %s checked out by patron %s", isbn, patronId));
    }

    public void returnBook(String patronId, String isbn, String branchId) {
        Loan loan = loans.stream()
            .filter(l -> l.getPatron().getPatronId().equals(patronId) 
                && l.getBook().getIsbn().equals(isbn) 
                && !l.isReturned())
            .findFirst()
            .orElse(null);

        if (loan != null) {
            loan.returnBook();
            LibraryBranch branch = branches.get(branchId);
            if (branch != null) {
                branch.addBook(isbn, 1);
                Logger.info(String.format("Book %s returned by patron %s", isbn, patronId));
                notifyReservations(isbn, branchId);
            }
        }
    }

    // Reservation System
    public void reserveBook(String patronId, String isbn, String branchId) {
        Patron patron = patrons.get(patronId);
        Book book = books.get(isbn);
        
        if (patron != null && book != null) {
            String key = isbn + ":" + branchId;
            reservations.putIfAbsent(key, new LinkedList<>());
            reservations.get(key).offer(new Reservation(book, patron, branchId));
            Logger.info(String.format("Book %s reserved by patron %s", isbn, patronId));
        }
    }

    private void notifyReservations(String isbn, String branchId) {
        String key = isbn + ":" + branchId;
        Queue<Reservation> queue = reservations.get(key);
        
        if (queue != null && !queue.isEmpty()) {
            LibraryBranch branch = branches.get(branchId);
            if (branch != null && branch.getAvailableQuantity(isbn) > 0) {
                Reservation reservation = queue.poll();
                if (reservation != null && !reservation.isNotified()) {
                    reservation.setNotified(true);
                    notifyObservers(reservation.getBook(), branchId);
                }
            }
        }
    }

    // Observer Pattern
    public void addObserver(BookAvailabilityObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Book book, String branchId) {
        for (BookAvailabilityObserver observer : observers) {
            observer.onBookAvailable(book, branchId);
        }
    }

    // Recommendation System
    public List<Book> getRecommendations(String patronId) {
        Patron patron = patrons.get(patronId);
        if (patron == null) return Collections.emptyList();
        return recommendationService.recommendBooks(patron, patrons.values(), books.values());
    }

    // Inventory
    public Map<String, Integer> getBranchInventory(String branchId) {
        LibraryBranch branch = branches.get(branchId);
        return branch != null ? branch.getInventory() : Collections.emptyMap();
    }

    public List<Loan> getActiveLoans() {
        return loans.stream().filter(l -> !l.isReturned()).collect(Collectors.toList());
    }

    public Book getBook(String isbn) {
        return books.get(isbn);
    }

    public Patron getPatron(String patronId) {
        return patrons.get(patronId);
    }
}
