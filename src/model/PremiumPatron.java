package model;

public class PremiumPatron extends Patron {
    public PremiumPatron(String patronId, String name, String email) {
        super(patronId, name, email);
    }

    @Override
    public int getMaxBooksAllowed() { return 10; }
}
