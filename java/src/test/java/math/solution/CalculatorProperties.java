package math.solution;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static math.Calculator.add;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitQuickcheck.class)
public class CalculatorProperties {
    @Property
    public void commutativity(int x, int y) {
        assertThat(add(x, y)).isEqualTo(add(y, x));
    }

    @Property
    public void associativity(int x) {
        assertThat(add(add(x, 1), 1)).isEqualTo(add(x, 2));
    }

    @Property
    public void identity(int x) {
        assertThat(add(x, 0)).isEqualTo(x);
    }
}