using System;
using FluentAssertions;
using FsCheck;
using FsCheck.Xunit;
using Xunit;
using static PBTKata.Math.Calculator;

namespace PBTKata.Tests.Math
{
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
}