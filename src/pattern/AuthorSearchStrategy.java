package pattern;

import model.Book;
import java.util.*;
import java.util.stream.Collectors;

public class AuthorSearchStrategy implements SearchStrategy {
    @Override
    public List<Book> search(Collection<Book> books, String query) {
        return books.stream()
            .filter(b -> b.getAuthor().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }
}
