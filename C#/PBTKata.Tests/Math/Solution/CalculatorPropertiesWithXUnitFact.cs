using FsCheck;
using FsCheck.Xunit;
using Xunit;
using Xunit.Abstractions;
using static PBTKata.Math.Calculator;

namespace PBTKata.Tests.Math.Solution
{
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
}