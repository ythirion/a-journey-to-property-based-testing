using FsCheck;
using FsCheck.Xunit;
using static PBTKata.Math.Calculator;

namespace PBTKata.Tests.Math.Solution
{
    public class CalculatorProperties
    {
        // We can collect data on generated inputs through Colect method
        [Property]
        public Property Commutativity(int x, int y)
            => (Add(x, y) == Add(y, x)).ToProperty().Collect($"x={x},y={y}");

        [Property]
        public Property Associativity(int x)
            => (Add(Add(x, 1), 1) == Add(x, 2)).ToProperty();

        [Property]
        public Property Identity(int x)
            => (Add(x, 0) == x).ToProperty();
    }
}