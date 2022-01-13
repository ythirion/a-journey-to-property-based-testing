package math.solution;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static java.util.stream.IntStream.range;
import static math.Calculator.add;
import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTests {
    private static final int TIMES = 100;
    private final Random random = new Random();

    @Test
    void should_return_4_when_I_add_1_to_3() {
        assertThat(add(1, 3)).isEqualTo(4);
    }

    @Test
    void should_return_2_when_I_add_Minus1_to_3() {
        assertThat(add(-1, 3)).isEqualTo(2);
    }

    @Test
    void should_return_99_when_I_add_0_to_99() {
        assertThat(add(0, 99)).isEqualTo(99);
    }

    @Test
    void should_return_their_correct_sum_when_I_add_2_random_numbers() {
        range(0, 100)
                .forEach(i -> {
                    val x = random.nextInt();
                    val y = random.nextInt();

                    assertThat(add(x, y)).isEqualTo(x + y);
                });
    }

    // Express addition properties in tests
    @Test
    void when_I_add_2_numbers_the_result_should_not_depend_on_parameter_order() {
        range(0, TIMES)
                .map(i -> random.nextInt())
                .forEach(x -> {
                    val y = random.nextInt();
                    assertThat(add(x, y)).isEqualTo(add(y, x));
                });
    }

    @Test
    void when_I_add_1_twice_is_the_same_as_adding_2_once() {
        range(0, TIMES)
                .map(i -> random.nextInt())
                .forEach(x -> assertThat(add(add(x, 1), 1)).isEqualTo(add(x, 2)));
    }

    @Test
    void when_I_add_0_to_a_random_number_is_the_same_than_doing_nothing_on_this_number() {
        range(0, TIMES)
                .map(i -> random.nextInt())
                .forEach(x -> assertThat(add(x, 0)).isEqualTo(x));
    }
}
