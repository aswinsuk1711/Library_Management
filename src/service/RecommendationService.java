package service;

import model.*;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {
    public List<Book> recommendBooks(Patron patron, Collection<Patron> allPatrons, Collection<Book> allBooks) {
        Set<String> patronBooks = patron.getBorrowingHistory().stream()
            .map(loan -> loan.getBook().getIsbn())
            .collect(Collectors.toSet());

        Map<String, Integer> bookScores = new HashMap<>();

        for (Patron other : allPatrons) {
            if (other.equals(patron)) continue;

            Set<String> otherBooks = other.getBorrowingHistory().stream()
                .map(loan -> loan.getBook().getIsbn())
                .collect(Collectors.toSet());

            Set<String> commonBooks = new HashSet<>(patronBooks);
            commonBooks.retainAll(otherBooks);

            if (!commonBooks.isEmpty()) {
                for (String isbn : otherBooks) {
                    if (!patronBooks.contains(isbn)) {
                        bookScores.merge(isbn, commonBooks.size(), Integer::sum);
                    }
                }
            }
        }

        return bookScores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(5)
            .map(entry -> findBookByIsbn(allBooks, entry.getKey()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private Book findBookByIsbn(Collection<Book> books, String isbn) {
        return books.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
    }
}
