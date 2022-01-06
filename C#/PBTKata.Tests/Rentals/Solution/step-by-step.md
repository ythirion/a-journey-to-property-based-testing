
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

    public Either<String, String> CalulateRental()
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
    => (new RentalCalculator(rentals).CalulateRental() ==
        new NewRentalCalculator(rentals).CalulateRental())
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
// Why would we need to instantiate a new calculator with Rentals at each call ?'
// Not a calculator -> String as return
class NewRentalCalculator(val rentals: List[Rental]) {
  // var is evil
  private var _amount     = .0
  private var _calculated = false

  // Do not need a state to make a query
  def amount(): Double = _amount
  def calculated(): Boolean = _calculated

  // this function breaks the Command Query separation principle
  // Not a pure function
  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty)
      Left("No rentals !!!")
    else {
      // could be simplified by a fold
      val result = new StringBuilder

      for (rental <- rentals) {
        // mutation is evil
        // checking the state here...
        if (!_calculated) this._amount += rental.amount
        result.append(formatLine(rental, _amount))
      }
      result.append(f"Total amount | ${this._amount}%.2f")
      
      // mutation is evil
      _calculated = true

      Right(result.toString)
    }
  }

  // Unused amount
  private def formatLine(rental: Rental, amount: Double) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"
}
```

* Remove state / mutation
```csharp
class NewRentalCalculator(val rentals: List[Rental]) {
  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty)
      Left("No rentals !!!")
    else {
      val result = new StringBuilder
      var amount = 0d

      for (rental <- rentals) {
        amount += rental.amount
        result.append(formatLine(rental, amount))
      }
      result.append(f"Total amount | ${amount}%.2f")

      Right(result.toString)
    }
  }

  private def formatLine(rental: Rental, amount: Double) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"
}
```

* Remove amount

```csharp
class NewRentalCalculator(val rentals: List[Rental]) {
  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty)
      Left("No rentals !!!")
    else {
      val result = new StringBuilder

      for (rental <- rentals) {
        result.append(formatLine(rental))
      }
      result.append(f"Total amount | ${rentals.map(_.amount).sum}%.2f")

      Right(result.toString)
    }
  }

  private def formatLine(rental: Rental) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"
}
```

* Use fold to simplify iteration on rentals

```csharp
class NewRentalCalculator(val rentals: List[Rental]) {
  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty) Left("No rentals !!!")
    else Right(statementFrom(rentals) + formatTotal(rentals))
  }

  private def statementFrom(rentals: List[Rental]): String =
    rentals.foldLeft("")((statement, rental) => statement + formatLine(rental))

  private def formatLine(rental: Rental) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"

  private def formatTotal(rentals: List[Rental]): String =
    f"Total amount | ${rentals.map(_.amount).sum}%.2f"
}
```

* Change Calculator to object / use better names

```csharp
object NewRentalStatementPrinter {
  def print(rentals: List[Rental]): Either[String, String] = {
    if (rentals.isEmpty) Left("No rentals !!!")
    else Right(statementFrom(rentals) + formatTotal(rentals))
  }

  private def statementFrom(rentals: List[Rental]): String =
    rentals.foldLeft("")((statement, rental) => statement + formatLine(rental))

  private def formatLine(rental: Rental) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"

  private def formatTotal(rentals: List[Rental]): String =
    f"Total amount | ${rentals.map(_.amount).sum}%.2f"
}
```

* Adapt the call in the property as well

```csharp
  "new implementation" should "have the same result" in {
    check(forAll { rentals: List[Rental] =>
      new RentalCalculator(rentals).calculateRental ==
        NewRentalStatementPrinter.print(rentals)
    })
  }
```

We have now a new better implementation without having introduced regression and without having spent a lot of time to identify test cases and written those