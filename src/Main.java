import model.*;
import pattern.*;
import service.*;
import exception.*;
import util.Logger;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryService library = LibraryService.getInstance();

    public static void main(String[] args) {
        library.addObserver((book, branchId) -> 
            System.out.println("\n*** NOTIFICATION: Book '" + book.getTitle() + "' is now available at branch " + branchId + " ***\n"));
        
        initializeSystem();
        
        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter choice: ");
            
            try {
                switch (choice) {
                    case 1: bookManagementMenu(); break;
                    case 2: patronManagementMenu(); break;
                    case 3: branchManagementMenu(); break;
                    case 4: lendingMenu(); break;
                    case 5: searchMenu(); break;
                    case 6: viewReports(); break;
                    case 0: System.out.println("Goodbye!"); return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void initializeSystem() {
        library.addBranch(new LibraryBranch("B001", "Main Branch"));
        library.addBranch(new LibraryBranch("B002", "East Branch"));
        System.out.println("System initialized with 2 branches.");
    }

    private static void displayMainMenu() {
        System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
        System.out.println("1. Book Management");
        System.out.println("2. Patron Management");
        System.out.println("3. Branch Management");
        System.out.println("4. Lending Operations");
        System.out.println("5. Search Books");
        System.out.println("6. View Reports");
        System.out.println("0. Exit");
        System.out.println("===============================================");
    }

    private static void bookManagementMenu() throws BookNotFoundException {
        System.out.println("\n--- Book Management ---");
        System.out.println("1. Add Book");
        System.out.println("2. Update Book");
        System.out.println("3. Remove Book");
        System.out.println("4. View All Books");
        System.out.println("5. Add Book to Branch Inventory");
        int choice = getIntInput("Enter choice: ");

        switch (choice) {
            case 1: addBook(); break;
            case 2: updateBook(); break;
            case 3: removeBook(); break;
            case 4: viewAllBooks(); break;
            case 5: addBookToBranch(); break;
        }
    }

    private static void addBook() {
        System.out.println("\n--- Add New Book ---");
        String isbn = getInput("ISBN: ");
        String title = getInput("Title: ");
        String author = getInput("Author: ");
        int year = getIntInput("Publication Year: ");
        
        Book book = new Book(isbn, title, author, year);
        library.addBook(book);
        System.out.println("Book added successfully!");
    }

    private static void updateBook() throws BookNotFoundException {
        System.out.println("\n--- Update Book ---");
        String isbn = getInput("Enter ISBN: ");
        Book book = library.getBook(isbn);
        
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        
        System.out.println("Current: " + book);
        String title = getInput("New Title (press Enter to skip): ");
        String author = getInput("New Author (press Enter to skip): ");
        String yearStr = getInput("New Year (press Enter to skip): ");
        
        library.updateBook(isbn, 
            title.isEmpty() ? book.getTitle() : title,
            author.isEmpty() ? book.getAuthor() : author,
            yearStr.isEmpty() ? book.getPublicationYear() : Integer.parseInt(yearStr));
        
        System.out.println("Book updated successfully!");
    }

    private static void removeBook() throws BookNotFoundException {
        System.out.println("\n--- Remove Book ---");
        String isbn = getInput("Enter ISBN: ");
        library.removeBook(isbn);
        System.out.println("Book removed successfully!");
    }

    private static void viewAllBooks() {
        System.out.println("\n--- All Books ---");
        List<Book> books = library.searchBooks(new TitleSearchStrategy(), "");
        if (books.isEmpty()) {
            System.out.println("No books in system.");
        } else {
            books.forEach(b -> System.out.println(b));
        }
    }

    private static void addBookToBranch() {
        System.out.println("\n--- Add Book to Branch Inventory ---");
        String branchId = getInput("Branch ID (B001/B002): ");
        String isbn = getInput("Book ISBN: ");
        int quantity = getIntInput("Quantity: ");
        
        library.addBookToBranch(branchId, isbn, quantity);
        System.out.println("Book added to branch inventory!");
    }

    private static void patronManagementMenu() {
        System.out.println("\n--- Patron Management ---");
        System.out.println("1. Add Patron");
        System.out.println("2. Update Patron");
        System.out.println("3. View Patron History");
        System.out.println("4. View All Patrons");
        int choice = getIntInput("Enter choice: ");

        switch (choice) {
            case 1: addPatron(); break;
            case 2: updatePatron(); break;
            case 3: viewPatronHistory(); break;
            case 4: viewAllPatrons(); break;
        }
    }

    private static void addPatron() {
        System.out.println("\n--- Add New Patron ---");
        String patronId = getInput("Patron ID: ");
        String name = getInput("Name: ");
        String email = getInput("Email: ");
        System.out.println("Type: 1=Regular (3 books), 2=Premium (10 books)");
        int type = getIntInput("Type: ");
        
        Patron patron = PatronFactory.createPatron(
            type == 2 ? PatronFactory.PatronType.PREMIUM : PatronFactory.PatronType.REGULAR,
            patronId, name, email);
        
        library.addPatron(patron);
        System.out.println("Patron added successfully!");
    }

    private static void updatePatron() {
        System.out.println("\n--- Update Patron ---");
        String patronId = getInput("Patron ID: ");
        Patron patron = library.getPatron(patronId);
        
        if (patron == null) {
            System.out.println("Patron not found!");
            return;
        }
        
        System.out.println("Current: " + patron);
        String name = getInput("New Name (press Enter to skip): ");
        String email = getInput("New Email (press Enter to skip): ");
        
        library.updatePatron(patronId,
            name.isEmpty() ? patron.getName() : name,
            email.isEmpty() ? patron.getEmail() : email);
        
        System.out.println("Patron updated successfully!");
    }

    private static void viewPatronHistory() {
        System.out.println("\n--- Patron Borrowing History ---");
        String patronId = getInput("Patron ID: ");
        List<Loan> history = library.getPatronHistory(patronId);
        
        if (history.isEmpty()) {
            System.out.println("No borrowing history.");
        } else {
            history.forEach(loan -> System.out.println(loan));
        }
    }

    private static void viewAllPatrons() {
        System.out.println("\n--- All Patrons ---");
        System.out.println("(Note: Use specific patron ID to view details)");
    }

    private static void branchManagementMenu() {
        System.out.println("\n--- Branch Management ---");
        System.out.println("1. Add Branch");
        System.out.println("2. View Branch Inventory");
        System.out.println("3. Transfer Books Between Branches");
        int choice = getIntInput("Enter choice: ");

        switch (choice) {
            case 1: addBranch(); break;
            case 2: viewBranchInventory(); break;
            case 3: transferBooks(); break;
        }
    }

    private static void addBranch() {
        System.out.println("\n--- Add New Branch ---");
        String branchId = getInput("Branch ID: ");
        String name = getInput("Branch Name: ");
        
        library.addBranch(new LibraryBranch(branchId, name));
        System.out.println("Branch added successfully!");
    }

    private static void viewBranchInventory() {
        System.out.println("\n--- Branch Inventory ---");
        String branchId = getInput("Branch ID: ");
        Map<String, Integer> inventory = library.getBranchInventory(branchId);
        
        if (inventory.isEmpty()) {
            System.out.println("No books in this branch.");
        } else {
            inventory.forEach((isbn, qty) -> {
                Book book = library.getBook(isbn);
                System.out.println(isbn + " - " + (book != null ? book.getTitle() : "Unknown") + ": " + qty + " copies");
            });
        }
    }

    private static void transferBooks() {
        System.out.println("\n--- Transfer Books ---");
        String fromBranch = getInput("From Branch ID: ");
        String toBranch = getInput("To Branch ID: ");
        String isbn = getInput("Book ISBN: ");
        int quantity = getIntInput("Quantity: ");
        
        if (library.transferBook(fromBranch, toBranch, isbn, quantity)) {
            System.out.println("Transfer successful!");
        } else {
            System.out.println("Transfer failed! Check branch IDs and availability.");
        }
    }

    private static void lendingMenu() throws BookNotFoundException, BookUnavailableException, PatronLimitExceededException {
        System.out.println("\n--- Lending Operations ---");
        System.out.println("1. Checkout Book");
        System.out.println("2. Return Book");
        System.out.println("3. Reserve Book");
        System.out.println("4. View Active Loans");
        int choice = getIntInput("Enter choice: ");

        switch (choice) {
            case 1: checkoutBook(); break;
            case 2: returnBook(); break;
            case 3: reserveBook(); break;
            case 4: viewActiveLoans(); break;
        }
    }

    private static void checkoutBook() throws BookNotFoundException, BookUnavailableException, PatronLimitExceededException {
        System.out.println("\n--- Checkout Book ---");
        String patronId = getInput("Patron ID: ");
        String isbn = getInput("Book ISBN: ");
        String branchId = getInput("Branch ID: ");
        
        library.checkoutBook(patronId, isbn, branchId);
        System.out.println("Book checked out successfully!");
    }

    private static void returnBook() {
        System.out.println("\n--- Return Book ---");
        String patronId = getInput("Patron ID: ");
        String isbn = getInput("Book ISBN: ");
        String branchId = getInput("Branch ID: ");
        
        library.returnBook(patronId, isbn, branchId);
        System.out.println("Book returned successfully!");
    }

    private static void reserveBook() {
        System.out.println("\n--- Reserve Book ---");
        String patronId = getInput("Patron ID: ");
        String isbn = getInput("Book ISBN: ");
        String branchId = getInput("Branch ID: ");
        
        library.reserveBook(patronId, isbn, branchId);
        System.out.println("Book reserved successfully! You'll be notified when available.");
    }

    private static void viewActiveLoans() {
        System.out.println("\n--- Active Loans ---");
        List<Loan> loans = library.getActiveLoans();
        
        if (loans.isEmpty()) {
            System.out.println("No active loans.");
        } else {
            loans.forEach(loan -> System.out.println(loan));
        }
    }

    private static void searchMenu() {
        System.out.println("\n--- Search Books ---");
        System.out.println("1. Search by Title");
        System.out.println("2. Search by Author");
        System.out.println("3. Search by ISBN");
        int choice = getIntInput("Enter choice: ");
        
        String query = getInput("Enter search term: ");
        List<Book> results = null;
        
        switch (choice) {
            case 1: results = library.searchBooks(new TitleSearchStrategy(), query); break;
            case 2: results = library.searchBooks(new AuthorSearchStrategy(), query); break;
            case 3: results = library.searchBooks(new ISBNSearchStrategy(), query); break;
        }
        
        if (results == null || results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("\nSearch Results:");
            results.forEach(book -> System.out.println(book));
        }
    }

    private static void viewReports() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. View All Active Loans");
        System.out.println("2. Get Book Recommendations");
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1: viewActiveLoans(); break;
            case 2: getRecommendations(); break;
        }
    }

    private static void getRecommendations() {
        System.out.println("\n--- Book Recommendations ---");
        String patronId = getInput("Patron ID: ");
        List<Book> recommendations = library.getRecommendations(patronId);
        
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available. Borrow more books to get recommendations!");
        } else {
            System.out.println("\nRecommended Books:");
            recommendations.forEach(book -> System.out.println(book));
        }
    }

    private static String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}
