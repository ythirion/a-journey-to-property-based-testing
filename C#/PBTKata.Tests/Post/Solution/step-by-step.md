## Postal Parcel
* Which kind of properties could we identify ?

```csharp
public record PostalParcel
{
    public const double MaxWeight = 20;
    public const double MinDeliveryCost = 1.99;
    public const double MaxDeliveryCost = 4.99;

    public double Weight { get; private init; }
    public static Option<PostalParcel> From(double weight) =>
        weight > 0 ? new PostalParcel { Weight = weight } : None;
}

public static class PostalParcelService
{
    public static Option<double> CalculateDeliveryCosts(Option<PostalParcel> postalParcel) =>
        postalParcel.Map(p => p.Weight > PostalParcel.MaxWeight ? PostalParcel.MaxDeliveryCost : PostalParcel.MinDeliveryCost);
}
```

* for all `Weight` <= 0 `delivery cost` should be None
* for all `Weight` > MaxWeight `delivery cost` should be max
* for all `Weight` <= MaxWeight `delivery cost` should be min

* To check our properties we need to either :
  * Filter our inputs with pre-condition (`Filter` method on Arbitrary<double>) :

```csharp
[Fact]
public void MaxDeliveryCostWhenWeightGreaterThanMaxWeight()
    => Prop.ForAll(Arb.Default.Float().Filter(x => x > MaxWeight),
            weight => CalculateCost(weight) == MaxDeliveryCost)
            .QuickCheckThrowOnFailure();
```

* Or create custom Arbitrary

```csharp
private Arbitrary<double> greaterThanMaxWeight =
    Arb.Default.Float().MapFilter(x => System.Math.Abs(x), x => x > MaxWeight);
 
[Fact]
public void MaxDeliveryCostWhenWeightGreaterThanMaxWeight()
    => Prop.ForAll(greaterThanMaxWeight,
            weight => CalculateCost(weight) == MaxDeliveryCost)
            .QuickCheckThrowOnFailure();
```

* Example of Custom Generator by using `PropertyAttribute` :
	* With `Property` we must :
		* Create a static class
		* Add a static method that returns `Arbitrary<OurType>`

```csharp
private static class GreaterThanMaxWeightGenerator {
    public static Arbitrary<double> Generate() =>
        Arb.Default.Float().MapFilter(x => System.Math.Abs(x), x => x > MaxWeight);
}

[Property(Arbitrary = new[] { typeof(GreaterThanMaxWeightGenerator) })]
public Property MaxDeliveryCostWhenWeightGreaterThanMaxWeight(double weight)
    => (CalculateCost(weight) == MaxDeliveryCost).ToProperty();
```    

* Let's use pre-conditions for this example :

```csharp
public class PostalParcelProperties
{       
    [Fact]
    public void MaxDeliveryCostWhenWeightGreaterThanMaxWeight()
        => Prop.ForAll(Arb.Default.Float().Filter(x => x > MaxWeight),
                weight => CalculateCost(weight) == MaxDeliveryCost)
                .QuickCheckThrowOnFailure();

    [Fact]
    public void MinDeliveryCostWhenWeightLowerOrEqualsMaxWeight()
        => Prop.ForAll(Arb.Default.Float().Filter(x => x > 0 && x <= MaxWeight),
                weight => CalculateCost(weight) == MinDeliveryCost)
                .QuickCheckThrowOnFailure();

    [Fact]
    public void NoneWhenWeightLowerOrEquals0()
        => Prop.ForAll(Arb.Default.Float().Filter(x => x <= 0),
                weight => CalculateCost(weight) == None)
                .QuickCheckThrowOnFailure();

    private static Option<double> CalculateCost(double weight)
        => CalculateDeliveryCosts(From(weight));
}
```