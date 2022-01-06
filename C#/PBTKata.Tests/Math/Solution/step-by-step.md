## Calculator
* We use [FsCheck](https://fscheck.github.io/FsCheck/) to implement our `Properties`
* It integrates well with `XUnit` through `FsCheck.XUnit`

With FsCheck we have 2 options when writing properties :

### Use PropertyAttribute
* We define our properties by creating methods :
    * annotated with the `PropertyAttribute`
    * taking random inputs as parameters
    * returning a Property
        * FsCheck define extension method on bool : `ToProperty()`

```csharp
public class CalculatorProperties
{
    [Property]
    public Property Commutativity(int x, int y)
        => (Add(x, y) == Add(y, x)).ToProperty();

    [Property]
    public Property Associativity(int x)
        => (Add(Add(x, 1), 1) == Add(x, 2)).ToProperty();

    [Property]
    public Property Identity(int x)
        => (Add(x, 0) == x).ToProperty();
}
```

* We can collect data on the inputs with the `Collect` method :

```csharp
[Property]
public Property Commutativity(int x, int y)
    => (Add(x, y) == Add(y, x)).ToProperty().Collect($"x={x},y={y}");
```

> With this option it is harder to pass custom Generator to create random inputs (look at PostalParcel example)

### Use XUnit Fact
* We define our tests as usual with XUnit
    * We use `Prop.ForAll` to define our properties
    * We check them with the `QuickCheckThrowOnFailure()` method
        * That will throw an exception in case of unsatisfied property

```csharp
public class CalculatorPropertiesWithXUnitFact
{
    [Fact]
    public void Commutativity()
        => Prop.ForAll<int, int>((x, y) => Add(x, y) == Add(y, x))
                .QuickCheckThrowOnFailure();

    [Fact]
    public void Associativity()
        => Prop.ForAll<int>(x => Add(Add(x, 1), 1) == Add(x, 2))
                .QuickCheckThrowOnFailure();

    [Fact]
    public void Identity()
        => Prop.ForAll<int>(x => Add(x, 0) == x)
                .QuickCheckThrowOnFailure();
}
```