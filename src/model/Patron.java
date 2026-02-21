package model;

import java.util.*;

public abstract class Patron {
    private final String patronId;
    private String name;
    private String email;
    private final List<Loan> borrowingHistory;

    public Patron(String patronId, String name, String email) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.borrowingHistory = new ArrayList<>();
    }

    public String getPatronId() { return patronId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Loan> getBorrowingHistory() { return new ArrayList<>(borrowingHistory); }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void addLoan(Loan loan) { borrowingHistory.add(loan); }

    public abstract int getMaxBooksAllowed();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patron)) return false;
        Patron patron = (Patron) o;
        return patronId.equals(patron.patronId);
    }

    @Override
    public int hashCode() { return Objects.hash(patronId); }

    @Override
    public String toString() {
        return String.format("Patron{id='%s', name='%s', email='%s'}", patronId, name, email);
    }
}
