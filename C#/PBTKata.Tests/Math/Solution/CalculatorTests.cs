using System;
using FluentAssertions;
using Xunit;
using static System.Linq.Enumerable;
using static PBTKata.Math.Calculator;

namespace PBTKata.Tests.Math.Solution
{
    public class CalculatorTests
    {
        private readonly Random _random = new();
        private const int times = 100;

        private int RandomInt() => _random.Next(int.MinValue, int.MaxValue);

        [Fact]
        public void Return4WhenIAdd1To3() => Add(1, 3).Should().Be(4);

        [Fact]
        public void Return2WhenIAddMinus1To3() => Add(-1, 3).Should().Be(2);

        [Fact]
        public void Return99WhenIAdd0To99() => Add(99, 0).Should().Be(99);

        [Fact]
        public void ReturnTheirCorrectSumWhenIAdd2RandomNumbers() =>
            Range(0, 100)
                .Map(_ => (x: RandomInt(), y: RandomInt()))
                .Iter(p => Add(p.x, p.y).Should().Be(p.x + p.y));

        // Express addition properties in tests without any library
        [Fact]
        public void WhenIAdd2NumbersTheResultShouldNotDependOnParameterOrder() =>
            Range(0, times)
                .Map(_ => (x: RandomInt(), y: RandomInt()))
                .Iter(p => Add(p.x, p.y).Should().Be(Add(p.y, p.x)));

        [Fact]
        public void WhenIAdd1TwiceItsTheSameAsAdding2Once() =>
            Range(0, times)
                .Map(_ => RandomInt())
                .Iter(x => Add(Add(x, 1), 1).Should().Be(Add(x, 2)));

        [Fact]
        public void WhenIAdd0ToAnyIntIsTheSameThanDoingNothingOnThisNumber() =>
            Range(0, times)
                .Map(_ => RandomInt())
                .Iter(x => Add(x, 0).Should().Be(x));

        // We should use the RunProperty method to avoid duplication in our tests
        private void RunProperty(Action<int> property) =>
            Range(0, times)
                .Map(_ => RandomInt())
                .Iter(property);
    }
}