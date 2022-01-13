package bank;

import io.vavr.control.Either;
import lombok.experimental.UtilityClass;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

@UtilityClass
public class AccountService {
    public static Either<String, Account> withdraw(Account account, Withdraw command) {
        return hasAlreadyBeenApplied(account, command) ?
                right(account)
                : applyWithdraw(account, command);
    }

    private static Either<String, Account> applyWithdraw(Account account, Withdraw command) {
        if (exceedMaxWithdrawal(account, command))
            return left("Amount exceeding your limit of " + account.maxWithdrawal());
        else if (exceedBalance(account, command))
            return left("Insufficient balance to withdraw : " + command.amount().value());
        else return right(account.withdraw(command));
    }

    private static boolean hasAlreadyBeenApplied(Account account, Withdraw command) {
        return account.withdraws().contains(command);
    }

    private static boolean exceedMaxWithdrawal(Account account, Withdraw command) {
        return command.amount().value() >= account.maxWithdrawal();
    }

    private static boolean exceedBalance(Account account, Withdraw command) {
        return command.amount().value() > account.balance() && !account.isOverdraftAuthorized();
    }
}