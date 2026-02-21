package pattern;

import model.Book;

public interface BookAvailabilityObserver {
    void onBookAvailable(Book book, String branchId);
}
