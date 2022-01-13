package post.solution;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static io.vavr.API.Some;
import static io.vavr.control.Option.none;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeThat;
import static post.PostalParcel.*;
import static post.PostalParcelService.calculateDeliveryCosts;

@RunWith(JUnitQuickcheck.class)
public class PostalParcelProperties {
    @Property
    public void delivery_costs_should_be_max_when_weight_is_greater_than_MaxWeight(double weight) {
        assumeThat(weight, greaterThan(MAX_WEIGHT));
        assertThat(calculateDeliveryCosts(from(weight)))
                .isEqualTo(Some(MAX_DELIVERY_COSTS));
    }

    @Property
    public void delivery_costs_should_be_min_when_weight_is_less_than_MaxWeight(double weight) {
        assumeThat(weight, lessThan(MAX_WEIGHT));
        assertThat(calculateDeliveryCosts(from(weight)))
                .isEqualTo(Some(MIN_DELIVERY_COSTS));
    }

    @Property
    public void delivery_costs_should_be_None_when_weight_less_than0(int weight) {
        assumeThat(weight, lessThanOrEqualTo(0));
        assertThat(calculateDeliveryCosts(from(weight)))
                .isEqualTo(none());
    }
}