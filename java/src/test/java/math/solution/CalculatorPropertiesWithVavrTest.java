package math.solution;

import io.vavr.test.Arbitrary;
import io.vavr.test.Property;
import org.junit.Test;

import static math.Calculator.add;

public class CalculatorPropertiesWithVavrTest {
    private final int SIZE = 10_000, TRIES = 100;

    @Test
    public void commutativity() {
        Property.def("Addition is commutative")
                .forAll(Arbitrary.integer(), Arbitrary.integer())
                .suchThat((x, y) -> add(x, y) == add(y, x))
                .check(SIZE, TRIES)
                .assertIsSatisfied();
    }

    @Test
    public void associativity() {
        Property.def("Addition is associative")
                .forAll(Arbitrary.integer())
                .suchThat(x -> add(add(x, 1), 1) == add(x, 2))
                .check(SIZE, TRIES)
                .assertIsSatisfied();
    }

    @Test
    public void identity() {
        Property.def("0 is the identity")
                .forAll(Arbitrary.integer())
                .suchThat(x -> add(x, 0) == x)
                .check(SIZE, TRIES)
                .assertIsSatisfied();
    }
}