package rentals.solution;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import rentals.Rental;
import rentals.RentalCalculator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitQuickcheck.class)
public class RentalNewImplementationProperties {
    @Property
    public void new_implementation_should_return_the_same_result(
            List<@From(RentalGenerator.class) Rental> rentals) {
        assertThat(new RentalCalculator(rentals).calculateRental())
                .isEqualTo(StatementPrinter.calculateRental(io.vavr.collection.List.ofAll(rentals)));
    }
}