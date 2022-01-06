using System;
using FsCheck;
using PBTKata.Bank;

namespace PBTKata.Tests.Bank.Solution
{
    public static class AccountBuilder
    {
        public static Account Empty() => new(100m.ToAmountUnsafe(), false, 200m.ToAmountUnsafe(), null);
    }

    public static class AccountExtensions
    {
        private static Amount ArbitraryAmount() =>
            Arb.Default.Decimal()
                .Filter(x => x > 0)
                .Generator
                .Sample(1, 1)
                .Head
                .ToAmountUnsafe();

        public static Account WithEnoughMoney(this Account account, Withdraw command) =>
            account with { Balance = command.Amount + ArbitraryAmount() };

        public static Account WithInsufficientBalance(this Account account, Withdraw command) =>
            account with { Balance = command.Amount - ArbitraryAmount() };

        public static Account WithdrawAmountReachingMaxWithdrawal(this Account account, Withdraw command) =>
            account with { MaxWithdrawal = command.Amount - ArbitraryAmount() };

        public static Account WithoutReachingMaxWithdrawal(this Account account, Withdraw command) =>
            account with { MaxWithdrawal = command.Amount + ArbitraryAmount() };

        public static Account WithoutOverDraftAuthorized(this Account account) =>
            account with { IsOverdraftAuthorized = false };

        public static Account WithOverDraftAuthorized(this Account account) =>
            account with { IsOverdraftAuthorized = true };
    }
}

