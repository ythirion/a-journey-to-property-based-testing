using LanguageExt;
using static LanguageExt.Prelude;

namespace PBTKata.Bank
{
    public static class AccountService
    {
        public static Either<String, Account> Withdraw(Account account, Withdraw command) =>
            HasAlreadyBeenApplied(account, command) ? account : Apply(account, command);

        private static Either<String, Account> Apply(Account account, Withdraw command)
        {
            if (ExceedMaxwithdrawal(account, command))
                return Left($"Amount exceeding your limit of {account.MaxWithdrawal}");
            else if (ExceedBalance(account, command))
                return Left($"Insufficient balance to withdraw : {command.Amount.Value}");
            else return Right(account.Withdraw(command));
        }

        private static bool HasAlreadyBeenApplied(Account account, Withdraw command) => account.Withdraws.Contains(command);
        private static bool ExceedMaxwithdrawal(Account account, Withdraw command) => command.Amount >= account.MaxWithdrawal;
        private static bool ExceedBalance(Account account, Withdraw command) => command.Amount.Value > account.Balance && !account.IsOverdraftAuthorized;
    }
}