
## Rental Calculator
* We duplicate the current implementation

```csharp
public class NewRentalCalculator
{
    public List<Rental> Rentals { get; init; }
    public bool Calculated { get { return calculated; } }
    public double Amount { get { return amount; } }

    private double amount = 0;
    private bool calculated = false;

    public NewRentalCalculator(List<Rental> rentals)
    {
        Rentals = rentals;
    }

    public Either<String, String> CalculateRental()
    {
        if(Rentals.IsNull() || Rentals.Count == 0)
        {
            return Left("No rentals !!!");
        }
        else
        {
            var result = new StringBuilder();

            foreach (var rental in Rentals)
            {
                if(!calculated)
                {
                    this.amount += rental.Amount;
                    result.AppendLine(FormatLine(rental, amount));
                }
            }
            result.AppendLine($"Total amount | {this.amount:N2}");
            calculated = true;

            return Right(result.ToString());
        }
    }

    private static String FormatLine(Rental rental, Double amount) =>
        $"{rental.Date} : {rental.Label} | {rental.Amount:N2}";
}
```

* We create the property that checks `f(x) == new_f(x)`

```csharp
[Property]
public Property NewImplementationShouldReturnTheSameResult(List<Rental> rentals)
    => (new RentalCalculator(rentals).CalculateRental() ==
        new NewRentalCalculator(rentals).CalculateRental())
    .ToProperty();
```

* The property should succeed
  * Let's check the robustness of our property by introducing defect(s) in our new implementation
    * Mutate one or several lines
  * The test must fail : that's great our property is acting as a safety net for our refactoring

`Seeing a test failing is as important as seeing it passing`

### Step-by-step refactoring
* Identify code smells

```csharp
// Why would we need to instantiate a new calculator with Rentals at each call ?
// Not a calculator -> String as return
public class NewRentalCalculator
{
    // Avoid using List : favor Immutable Collections
    public List<Rental> Rentals { get; init; }
    // Why expose states to the outside world ?
    public bool Calculated { get { return calculated; } }
    public double Amount { get { return amount; } }

    // Do not need a state to make a query...
    private double amount = 0;
    private bool calculated = false;

    public NewRentalCalculator(List<Rental> rentals)
    {
        Rentals = rentals;
    }

    // this function breaks the Command Query separation principle
    // Not a pure function : lie to us because it changes internal state without saying it
    public Either<String, String> CalculateRental()
    {
        if(Rentals.IsNull() || Rentals.Count == 0)
        {
            return Left("No rentals !!!");
        }
        else
        {
            // We could fold the collection instead of instantiating this StringBuilder
            var result = new StringBuilder();

            // Old school loop : favor fold for this purpose
            foreach (var rental in Rentals)
            {
                // checking internal state
                if(!calculated)
                {
                    // mutate internal state
                    this.amount += rental.Amount;
                    result.AppendLine(FormatLine(rental, amount));
                }
            }
            result.AppendLine($"Total amount | {this.amount:N2}");

            // mutation is evil
            calculated = true;

            return Right(result.ToString());
        }
    }

    // unused amount parameter
    private static String FormatLine(Rental rental, Double amount) =>
        $"{rental.Date} : {rental.Label} | {rental.Amount:N2}";
}
```

* Remove state / mutation (amount and calculated)
```csharp
public class NewRentalCalculator
{
    public List<Rental> Rentals { get; init; }

    public NewRentalCalculator(List<Rental> rentals)
    {
        Rentals = rentals;
    }

    public Either<String, String> CalculateRental()
    {
        if(Rentals.IsNull() || Rentals.Count == 0)
        {
            return Left("No rentals !!!");
        }
        else
        {
            var amount = 0d;
            var result = new StringBuilder();

            foreach (var rental in Rentals)
            {
                amount += rental.Amount;
                result.AppendLine(FormatLine(rental, amount));
            }
            result.AppendLine($"Total amount | {amount:N2}");

            return Right(result.ToString());
        }
    }

    private static String FormatLine(Rental rental, Double amount) =>
        $"{rental.Date} : {rental.Label} | {rental.Amount:N2}";
}
```

* Review encapsulation and change rentals type (Use `Seq<Rental>`)
```csharp
public class NewRentalCalculator
{
    private readonly Seq<Rental> rentals;

    public NewRentalCalculator(List<Rental> rentals) =>
        this.rentals = rentals.ToSeq();

    public Either<String, String> CalculateRental()
    {
        if (rentals.IsEmpty)
        {
            return Left("No rentals !!!");
        }
        else
        {
            var amount = 0d;
            var result = new StringBuilder();

            foreach (var rental in rentals)
            {
                amount += rental.Amount;
                result.AppendLine(FormatLine(rental, amount));
            }
            result.AppendLine($"Total amount | {amount:N2}");

            return Right(result.ToString());
        }
    }

    private static String FormatLine(Rental rental, Double amount) =>
        $"{rental.Date} : {rental.Label} | {rental.Amount:N2}";
}
```

* Remove amount and use Seq<Rental>

```csharp
public class NewRentalCalculator
{
    private readonly Seq<Rental> rentals;

    public NewRentalCalculator(List<Rental> rentals) =>
        this.rentals = rentals.ToSeq();

    public Either<String, String> CalculateRental()
    {
        if (rentals.IsEmpty)
        {
            return Left("No rentals !!!");
        }
        else
        {
            var result = new StringBuilder();

            foreach (var rental in rentals)
            {
                result.AppendLine(FormatLine(rental));
            }
            result.AppendLine($"Total amount | {rentals.Sum(r => r.Amount):N2}");

            return Right(result.ToString());
        }
    }

    private static String FormatLine(Rental rental) =>
        $"{rental.Date} : {rental.Label} | {rental.Amount:N2}";
}
```

* Use fold to simplify iteration on rentals

```csharp
public class NewRentalCalculator
{
    private readonly Seq<Rental> rentals;

    public NewRentalCalculator(List<Rental> rentals) =>
        this.rentals = rentals.ToSeq();

    public Either<String, String> CalculateRental()
    {
        if (rentals.IsEmpty)
        {
            return Left("No rentals !!!");
        }
        else
        {
            return Right(StatementFrom(rentals) + FormatTotal(rentals));
        }
    }

    private static String StatementFrom(Seq<Rental> rentals) =>
        rentals.Fold("", (statement, rental) => statement + FormatLine(rental));

    private static String FormatTotal(Seq<Rental> rentals) =>
        $"Total amount | {rentals.Sum(r => r.Amount):N2}{Environment.NewLine}";

    private static String FormatLine(Rental rental) =>
        $"{rental.Date} : {rental.Label} | {rental.Amount:N2}{Environment.NewLine}";
}
```

* Use better name / Make the function pure / clean the class

```csharp
public static class StatementPrinter
{
    public static Either<String, String> Print(Seq<Rental> rentals) =>
        rentals.IsEmpty ?
            Left("No rentals !!!") :
            Right(StatementFrom(rentals) + FormatTotal(rentals));

    private static String StatementFrom(Seq<Rental> rentals) =>
        rentals.Fold("", (statement, rental) => statement + FormatLine(rental));

    private static String FormatTotal(Seq<Rental> rentals) =>
        $"Total amount | {rentals.Sum(r => r.Amount):N2}{Environment.NewLine}";

    private static String FormatLine(Rental rental) =>
        $"{rental.Date} : {rental.Label} | {rental.Amount:N2}{Environment.NewLine}";
}
```

* Adapt the call in the property as well

```csharp
[Property]
public Property NewImplementationShouldReturnTheSameResult(List<Rental> rentals)
    => (new RentalCalculator(rentals).CalculateRental() ==
        StatementPrinter.Print(rentals.ToSeq()))
    .ToProperty();
```

* We have now a new better implementation without having introduced regression and without having spent a lot of time to identify test cases and written those
* We can now plug the callers and :
    * delete the old implementation
    * our `RentalNewImplementationProperties` file as well