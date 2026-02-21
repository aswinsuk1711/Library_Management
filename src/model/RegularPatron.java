package model;

public class RegularPatron extends Patron {
    public RegularPatron(String patronId, String name, String email) {
        super(patronId, name, email);
    }

    @Override
    public int getMaxBooksAllowed() { return 3; }
}
