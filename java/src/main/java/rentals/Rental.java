package rentals;

import java.time.LocalDate;

public record Rental(LocalDate date, String label, double amount) {
}