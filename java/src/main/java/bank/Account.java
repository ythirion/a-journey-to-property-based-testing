package bank;

import io.vavr.collection.Seq;

public record Account(
        double balance,
        boolean isOverdraftAuthorized,
        double maxWithdrawal,
        Seq<Withdraw> withdraws) {
    public Account withdraw(Withdraw command) {
        return new Account(balance - command.amount().value(),
                isOverdraftAuthorized,
                maxWithdrawal,
                withdraws().append(command));
    }
}