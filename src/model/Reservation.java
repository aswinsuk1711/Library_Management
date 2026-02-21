package model;

import java.time.LocalDateTime;

public class Reservation {
    private final Book book;
    private final Patron patron;
    private final String branchId;
    private final LocalDateTime reservationDate;
    private boolean notified;

    public Reservation(Book book, Patron patron, String branchId) {
        this.book = book;
        this.patron = patron;
        this.branchId = branchId;
        this.reservationDate = LocalDateTime.now();
        this.notified = false;
    }

    public Book getBook() { return book; }
    public Patron getPatron() { return patron; }
    public String getBranchId() { return branchId; }
    public LocalDateTime getReservationDate() { return reservationDate; }
    public boolean isNotified() { return notified; }
    public void setNotified(boolean notified) { this.notified = notified; }
}
