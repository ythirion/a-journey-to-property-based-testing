package rentals.solution;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.generator.java.time.LocalDateGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import rentals.Rental;

public class RentalGenerator extends Generator<Rental> {
    private final LocalDateGenerator dateGenerator = new LocalDateGenerator();
    private final StringGenerator stringGenerator = new StringGenerator();

    public RentalGenerator() {
        super(Rental.class);
    }

    @Override
    public Rental generate(SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
        return new Rental(
                dateGenerator.generate(sourceOfRandomness, generationStatus),
                stringGenerator.generate(sourceOfRandomness, generationStatus),
                Math.abs(sourceOfRandomness.nextDouble()));
    }
}
