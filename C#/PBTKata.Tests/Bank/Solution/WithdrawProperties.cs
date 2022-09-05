using System;
using FsCheck;
using LanguageExt;
using PBTKata.Bank;
using Xunit;

namespace PBTKata.Tests.Bank.Solution
{
    public class WithdrawProperties
    {
        [Fact]
        public void WithdrawShouldBeIdemPotent()
            => CheckProperty(
                (account, withdraw) => account.WithEnoughMoney(withdraw)
                    .WithoutReachingMaxWithdrawal(withdraw),
                (_, withdraw, debitedAccount) => AccountService.Withdraw(debitedAccount.RightValueUnsafe(), withdraw) ==
                                                 debitedAccount);

        [Fact]
        public void BalanceShouldBeDecrementedAtLeastFromTheWithdrawAmount()
            => CheckProperty(
                (account, withdraw) => account.WithEnoughMoney(withdraw)
                    .WithoutReachingMaxWithdrawal(withdraw),
                (account, withdraw, debitedAccount) => debitedAccount.RightValueUnsafe().Balance <=
                                                       account.Balance - withdraw.Amount.Value);

        [Fact]
        public void
            BalanceShouldBeDecrementedAtLeastFromTheWithdrawAmountWhenInsufficientBalanceButOverdraftAuthorized()
            => CheckProperty(
                (account, withdraw) => account.WithInsufficientBalance(withdraw)
                    .WithoutReachingMaxWithdrawal(withdraw)
                    .WithOverDraftAuthorized(),
                (account, withdraw, debitedAccount) => debitedAccount.RightValueUnsafe().Balance <=
                                                       account.Balance - withdraw.Amount.Value);

        [Fact]
        public void WithdrawShouldNotBeAllowedWhenWithdrawAmountGreaterThanMaxWithdrawal()
            => CheckProperty(
                (account, withdraw) => account.WithdrawAmountReachingMaxWithdrawal(withdraw),
                (_, _, debitedAccount) => debitedAccount.LeftValueUnsafe().StartsWith("Amount exceeding your limit"));

        [Fact]
        public void WithdrawShouldNotBeAllowedWhenInsufficientBalanceAndNoOverdraft()
            => CheckProperty(
                (account, withdraw) => account.WithInsufficientBalance(withdraw)
                    .WithoutOverDraftAuthorized()
                    .WithoutReachingMaxWithdrawal(withdraw),
                (_, _, debitedAccount) =>
                    debitedAccount.LeftValueUnsafe().StartsWith("Insufficient balance to withdraw"));

        private void CheckProperty(
            Func<Account, Withdraw, Account> accountConfiguration,
            Func<Account, Withdraw, Either<string, Account>, bool> property)
        {
            Prop.ForAll(_withdrawCommandGenerator,
                    (withdraw) =>
                    {
                        var account = accountConfiguration(AccountBuilder.Empty(), withdraw);
                        return property(account, withdraw, AccountService.Withdraw(account, withdraw));
                    })
                .QuickCheckThrowOnFailure();
        }

        private readonly Arbitrary<Withdraw> _withdrawCommandGenerator =
            (from clientId in Arb.Generate<Guid>()
                from amount in Arb.Default.Decimal().Filter(x => x > 0).Generator
                from requestDate in Arb.Generate<DateTime>()
                select new Withdraw(clientId, amount.ToAmountUnsafe(), requestDate)
            ).ToArbitrary();
    }
}