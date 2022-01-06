using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using LanguageExt;
using PBTKata.Bank;
using Xunit;
using static LanguageExt.Prelude;

namespace PBTKata.Tests.Bank.Solution
{
    public class WithdrawExampleTests
    {
        public static IEnumerable<object[]> Table => new List<object[]>
        {
            new object[] { 10_000m, false, 1200m, 1199.99m, Right(8800.01m) },
            new object[] { 0, true, 500m, 50m, Right(-50m) },
            new object[] { 1, false, 1200m, 1199.99m, Left("Insufficient balance to withdraw") },
            new object[] { 0, false, 500m, 50m, Left("Insufficient balance to withdraw") },
            new object[] { 10_000, true, 1200m, 1200.0001, Left("Amount exceeding your limit of") },
            new object[] { 0, false, 500m, 500m, Left("Amount exceeding your limit of") }
        };

        [Theory]
        [MemberData(nameof(Table))]
        public void WithdrawShould(
            decimal balance,
            bool isOverdraftAuthorized,
            decimal maxWithdrawal,
            decimal withdrawAmount,
            Either<String, decimal> expectedResult)
        {
            var command = new Withdraw(Guid.NewGuid(), withdrawAmount.ToAmountUnsafe(), DateTime.Now);
            var result = AccountService.Withdraw(
                new Account(balance, isOverdraftAuthorized, maxWithdrawal.ToAmountUnsafe(), ImmutableList<Withdraw>.Empty),
                command);

            expectedResult.Match(
                expectedBalance =>
                {
                    var debitedAccount = result.RightValueUnsafe();

                    debitedAccount.Balance.Should().Be(expectedBalance);
                    debitedAccount.Withdraws.Should().HaveCount(1)
                        .And.Contain(command);
                },
                expectedErrorMessage => result.LeftValueUnsafe().Should().StartWith(expectedErrorMessage));
        }
    }
}