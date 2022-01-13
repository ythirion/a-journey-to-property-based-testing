## Postal Parcel
* Which kind of properties could we identify ?

```java
public record PostalParcel(double weight) {
    public static final double MAX_WEIGHT = 20d;
    public static final double MAX_DELIVERY_COSTS = 4.99;
    public static final double MIN_DELIVERY_COSTS = 1.99;

    public static Option<PostalParcel> from(double weight) {
        return weight > 0 ? Some(new PostalParcel(weight)) : none();
    }
}

@UtilityClass
public class PostalParcelService {
    public static Option<Double> calculateDeliveryCosts(Option<PostalParcel> postalParcel) {
        return postalParcel.map(p -> p.weight() > MAX_WEIGHT ? MAX_DELIVERY_COSTS : MIN_DELIVERY_COSTS);
    }
}
```

* for all `Weight` <= 0 `delivery cost` should be None
* for all `Weight` > MaxWeight `delivery cost` should be max
* for all `Weight` <= MaxWeight `delivery cost` should be min

* To check our properties we need to use pre-conditions to constrain our inputs :
  * We do that by using `assumeThat` method

```java
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
```