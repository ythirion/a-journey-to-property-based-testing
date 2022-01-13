package bank;

import io.vavr.control.Option;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record Amount(double value) {
    public Amount {
        if (value < 0) throw new IllegalArgumentException("value must be a positive double");
    }

    public static Option<Amount> from(double amount) {
        return amount > 0 ? some(new Amount(amount)) : none();
    }
}