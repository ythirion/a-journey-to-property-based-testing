package post;

import io.vavr.control.Option;

import static io.vavr.API.Some;
import static io.vavr.control.Option.none;

public record PostalParcel(double weight) {
    public static final double MAX_WEIGHT = 20d;
    public static final double MAX_DELIVERY_COSTS = 4.99;
    public static final double MIN_DELIVERY_COSTS = 1.99;

    public static Option<PostalParcel> from(double weight) {
        return weight > 0 ? Some(new PostalParcel(weight)) : none();
    }
}

