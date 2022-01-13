
## Rental Calculator
* We duplicate the current implementation

```java
@Getter
public class NewRentalCalculator {
    private final List<Rental> rentals;
    private double amount;
    private boolean calculated;

    public NewRentalCalculator(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public Either<String, String> calculateRental() {
        if (rentals == null || rentals.size() == 0) {
            return left("No rentals !!!");
        }

        var result = new StringBuilder();

        for (var rental : rentals) {
            if (!calculated) {
                this.amount += rental.amount();
            }
            result.append(formatLine(rental, amount));
        }
        result.append(String.format("Total amount | %f", this.amount));
        calculated = true;

        return right(result.toString());
    }

    private String formatLine(Rental rental, double amount) {
        return String.format("%tF : %s | %f \n",
                rental.date(),
                rental.label(),
                rental.amount());
    }
}
```

* We create the property that checks `f(x) == new_f(x)`

```java
@Property
public void new_implementation_should_return_the_same_result(
        List<@From(RentalGenerator.class) Rental> rentals) {
    assertThat(new RentalCalculator(rentals).calculateRental())
            .isEqualTo(new NewRentalCalculator(rentals).calculateRental());
}
```

* We need to create a custom generator to create the `Rental` instances :
```java
public class RentalGenerator extends Generator<Rental> {
    private final LocalDateGenerator dateGenerator = new LocalDateGenerator();
    private final StringGenerator stringGenerator = new StringGenerator();

    public RentalGenerator() {
        super(Rental.class);
    }

    @Override
    public Rental generate(SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
        return new Rental(
                dateGenerator.generate(sourceOfRandomness, generationStatus),
                stringGenerator.generate(sourceOfRandomness, generationStatus),
                Math.abs(sourceOfRandomness.nextDouble()));
    }
}
```

* The property should succeed
  * Let's check the robustness of our property by introducing defect(s) in our new implementation
    * Mutate one or several lines
  * The test must fail : that's great our property is acting as a safety net for our refactoring

`Seeing a test failing is as important as seeing it passing`

### Step-by-step refactoring
* Identify code smells

```java
// Why exposing fields to the outside world ?
@Getter
// Why would we need to instantiate a new calculator with Rentals at each call ?
// Not a calculator -> String as return
public class NewRentalCalculator {
    // Avoid using List : favor Immutable Collections
    private final List<Rental> rentals;
    // Do not need a state to make a query...
    private double amount;
    // Why expose states to the outside world ?
    private boolean calculated;

    public NewRentalCalculator(List<Rental> rentals) {
        this.rentals = rentals;
    }

    // this function breaks the Command Query separation principle
    // Not a pure function : lie to us because it changes internal state without saying it 
    public Either<String, String> calculateRental() {
        if (rentals == null || rentals.size() == 0) {
            return left("No rentals !!!");
        }

        // We could fold the collection instead of instantiating this StringBuilder
        var result = new StringBuilder();

        // Old school loop : favor fold for this purpose
        for (var rental : rentals) {
            // checking internal state
            if (!calculated) {
                // mutate internal state
                this.amount += rental.amount();
            }
            result.append(formatLine(rental, amount));
        }
        result.append(String.format("Total amount | %f", this.amount));
        // mutation is evil
        calculated = true;

        return right(result.toString());
    }

    // unused amount parameter
    private String formatLine(Rental rental, double amount) {
        return String.format("%tF : %s | %f \n",
                rental.date(),
                rental.label(),
                rental.amount());
    }
}
```

* Remove state / mutation
  * amount / calculated
  * Remove `@Getter`
```java
public class NewRentalCalculator {
    private final List<Rental> rentals;

    public NewRentalCalculator(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public Either<String, String> calculateRental() {
        if (rentals == null || rentals.size() == 0) {
            return left("No rentals !!!");
        }

        val result = new StringBuilder();
        var amount = 0d;

        for (var rental : rentals) {
            amount += rental.amount();
            result.append(formatLine(rental, amount));
        }
        result.append(String.format("Total amount | %f", amount));

        return right(result.toString());
    }

    private String formatLine(Rental rental, double amount) {
        return String.format("%tF : %s | %f \n",
                rental.date(),
                rental.label(),
                rental.amount());
    }
}
```

* Review encapsulation and change rentals type (Use `Seq<Rental>`)
```java
public class NewRentalCalculator {
    private final Seq<Rental> rentals;

    public NewRentalCalculator(Seq<Rental> rentals) {
        this.rentals = rentals;
    }

    public Either<String, String> calculateRental() {
        if (rentals == null || rentals.size() == 0) {
            return left("No rentals !!!");
        }

        val result = new StringBuilder();
        var amount = 0d;

        for (var rental : rentals) {
            amount += rental.amount();
            result.append(formatLine(rental, amount));
        }
        result.append(String.format("Total amount | %f", amount));

        return right(result.toString());
    }

    private String formatLine(Rental rental, double amount) {
        return String.format("%tF : %s | %f \n",
                rental.date(),
                rental.label(),
                rental.amount());
    }
}
```

* Use ternary and extract right function

```java
public class NewRentalCalculator {
    private final Seq<Rental> rentals;

    public NewRentalCalculator(Seq<Rental> rentals) {
        this.rentals = rentals;
    }

    public Either<String, String> calculateRental() {
        return rentals.isEmpty() ? left("No rentals !!!") :
                formatRentals();
    }

    private Either<String, String> formatRentals() {
        val result = new StringBuilder();
        var amount = 0d;

        for (var rental : rentals) {
            amount += rental.amount();
            result.append(formatLine(rental, amount));
        }
        result.append(String.format("Total amount | %f", amount));

        return right(result.toString());
    }

    private String formatLine(Rental rental, double amount) {
        return String.format("%tF : %s | %f \n",
                rental.date(),
                rental.label(),
                rental.amount());
    }
}
```

* Make the function pure

```java
@UtilityClass
public class NewRentalCalculator {
    public static Either<String, String> calculateRental(Seq<Rental> rentals) {
        return rentals.isEmpty() ? left("No rentals !!!") :
                formatRentals(rentals);
    }

    private Either<String, String> formatRentals(Seq<Rental> rentals) {
        val result = new StringBuilder();
        var amount = 0d;

        for (var rental : rentals) {
            amount += rental.amount();
            result.append(formatLine(rental, amount));
        }
        result.append(String.format("Total amount | %f", amount));

        return right(result.toString());
    }

    private String formatLine(Rental rental, double amount) {
        return String.format("%tF : %s | %f \n",
                rental.date(),
                rental.label(),
                rental.amount());
    }
}
```

* Adapt the call in the property as well

```java
@Property
public void new_implementation_should_return_the_same_result(
        List<@From(RentalGenerator.class) Rental> rentals) {
    assertThat(new RentalCalculator(rentals).calculateRental())
            .isEqualTo(NewRentalCalculator.calculateRental(io.vavr.collection.List.ofAll(rentals)));
}
```

* Fold / use better names / clean the code
```java
@UtilityClass
public class StatementPrinter {
    public static Either<String, String> calculateRental(Seq<Rental> rentals) {
        return rentals.isEmpty() ? left("No rentals !!!") :
                right(statementFrom(rentals) + formatTotal(rentals));
    }

    private String statementFrom(Seq<Rental> rentals) {
        return rentals.foldLeft("", (statement, rental) -> statement + formatLine(rental));
    }

    private String formatTotal(Seq<Rental> rentals) {
        return String.format("Total amount | %f", rentals.map(Rental::amount).sum().doubleValue());
    }

    private String formatLine(Rental rental) {
        return String.format("%tF : %s | %f \n", rental.date(), rental.label(), rental.amount());
    }
}
```

* We have now a new better implementation without having introduced regression and without having spent a lot of time to identify test cases and written those
* We can now plug the callers and :
    * delete the old implementation
    * our `RentalNewImplementationProperties` file as well