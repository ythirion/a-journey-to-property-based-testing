package rentals;

import lombok.Getter;

import java.util.List;

@Getter
public class RentalCalculator {
    private final List<Rental> rentals;
    private double amount;
    private boolean calculated;

    public RentalCalculator(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public String calculateRental() {
        if (rentals.isEmpty()) {
            throw new IllegalStateException("No rentals !!!");
        }

        var result = new StringBuilder();

        for (var rental : rentals) {
            if (!calculated)
                this.amount += rental.amount();

            result.append(formatLine(rental, amount));
        }
        result.append(String.format("Total amount | %f", this.amount));
        calculated = true;

        return result.toString();
    }

    private String formatLine(Rental rental, double amount) {
        return String.format("%tF : %s | %f \n",
                rental.date(),
                rental.label(),
                rental.amount());
    }
}