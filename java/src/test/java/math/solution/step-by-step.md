## Calculator
We use [Junit QuickCheck](https://pholser.github.io/junit-quickcheck/site/1.0/) to implement our `Properties`

### Create our properties with Junit-quickcheck
* We define our properties by creating a new class :
  * and specify the runner `@RunWith(JUnitQuickcheck.class)`
  * annotate each property with the `Property` annotation
    * taking random inputs as parameters
   
```java
@RunWith(JUnitQuickcheck.class)
public class CalculatorProperties {
    @Property
    public void commutativity(int x, int y) {
        assertThat(add(x, y)).isEqualTo(add(y, x));
    }

    @Property
    public void associativity(int x) {
        assertThat(add(add(x, 1), 1)).isEqualTo(add(x, 2));
    }

    @Property
    public void identity(int x) {
        assertThat(add(x, 0)).isEqualTo(x);
    }
}
```

* We can collect data on the inputs by configuring our logger :
  * Add dependency on Sl4j as explained in the [documentation](https://pholser.github.io/junit-quickcheck/site/0.9.1/usage/verification-modes.html) :
    * You need to configure your logger in DEBUG mode
```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.32</version>
</dependency>
```

### Alternative : vavr-test
Alternatively we can use [`vavr-test`](https://github.com/vavr-io/vavr-test) to define our properties :
* No more runner on our class
* We define our tests as usual but use the Property functions from `vavr-test` to declare our properties

```java
public class CalculatorPropertiesWithVavrTest {
    private final int SIZE = 10_000, TRIES = 100;

    @Test
    public void commutativity() {
        Property.def("Addition is commutative")
                .forAll(Arbitrary.integer(), Arbitrary.integer())
                .suchThat((x, y) -> add(x, y) == add(y, x))
                .check(SIZE, TRIES)
                .assertIsSatisfied();
    }

    @Test
    public void associativity() {
        Property.def("Addition is associative")
                .forAll(Arbitrary.integer())
                .suchThat(x -> add(add(x, 1), 1) == add(x, 2))
                .check(SIZE, TRIES)
                .assertIsSatisfied();
    }

    @Test
    public void identity() {
        Property.def("0 is the identity")
                .forAll(Arbitrary.integer())
                .suchThat(x -> add(x, 0) == x)
                .check(SIZE, TRIES)
                .assertIsSatisfied();
    }
}
```