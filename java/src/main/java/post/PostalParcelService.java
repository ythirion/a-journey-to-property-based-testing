package post;

import io.vavr.control.Option;
import lombok.experimental.UtilityClass;

import static post.PostalParcel.*;

@UtilityClass
public class PostalParcelService {
    public static Option<Double> calculateDeliveryCosts(Option<PostalParcel> postalParcel) {
        return postalParcel.map(p -> p.weight() > MAX_WEIGHT ? MAX_DELIVERY_COSTS : MIN_DELIVERY_COSTS);
    }
}