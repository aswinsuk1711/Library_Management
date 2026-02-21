package pattern;

import model.Book;
import java.util.*;

public interface SearchStrategy {
    List<Book> search(Collection<Book> books, String query);
}
