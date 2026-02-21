package model;

import java.time.LocalDate;

public class Loan {
    private final Book book;
    private final Patron patron;
    private final String branchId;
    private final LocalDate checkoutDate;
    private LocalDate returnDate;

    public Loan(Book book, Patron patron, String branchId) {
        this.book = book;
        this.patron = patron;
        this.branchId = branchId;
        this.checkoutDate = LocalDate.now();
    }

    public Book getBook() { return book; }
    public Patron getPatron() { return patron; }
    public String getBranchId() { return branchId; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returnDate != null; }

    public void returnBook() { this.returnDate = LocalDate.now(); }

    @Override
    public String toString() {
        return String.format("Loan{book=%s, patron=%s, checkout=%s, returned=%s}", 
            book.getIsbn(), patron.getPatronId(), checkoutDate, isReturned());
    }
}
