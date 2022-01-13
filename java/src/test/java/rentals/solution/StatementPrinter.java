package rentals.solution;

import io.vavr.collection.Seq;
import io.vavr.control.Either;
import lombok.experimental.UtilityClass;
import rentals.Rental;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

@UtilityClass
public class StatementPrinter {
    public static Either<String, String> calculateRental(Seq<Rental> rentals) {
        return rentals.isEmpty() ? left("No rentals !!!") :
                right(statementFrom(rentals) + formatTotal(rentals));
    }

    private String statementFrom(Seq<Rental> rentals) {
        return rentals.foldLeft("", (statement, rental) -> statement + formatLine(rental));
    }

    private String formatTotal(Seq<Rental> rentals) {
        return String.format("Total amount | %f", rentals.map(Rental::amount).sum().doubleValue());
    }

    private String formatLine(Rental rental) {
        return String.format("%tF : %s | %f \n", rental.date(), rental.label(), rental.amount());
    }
}