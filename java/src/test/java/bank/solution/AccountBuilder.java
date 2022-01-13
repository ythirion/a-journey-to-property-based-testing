package bank.solution;

import bank.Account;
import bank.Withdraw;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import lombok.AllArgsConstructor;
import lombok.With;

import java.util.Random;

@With
@AllArgsConstructor
public class AccountBuilder {
    private static final Random random = new Random();
    private final double balance;
    private final boolean isOverdraftAuthorized;
    private final double maxWithdrawal;
    private final Seq<Withdraw> withdraws;

    public static AccountBuilder newAccount() {
        return new AccountBuilder(0, false, 0, List.empty());
    }

    private static Double arbitraryAmount() {
        return random.nextDouble();
    }

    public AccountBuilder withInsufficientBalance(Withdraw command) {
        return this.withBalance(command.amount().value() - arbitraryAmount());
    }

    public AccountBuilder withdrawAmountReachingMaxWithdrawal(Withdraw command) {
        return this.withMaxWithdrawal(command.amount().value() - arbitraryAmount());
    }

    public AccountBuilder withoutReachingMaxWithdrawal(Withdraw command) {
        return this.withMaxWithdrawal(command.amount().value() + arbitraryAmount());
    }

    public AccountBuilder withoutOverdraft() {
        return this.withOverdraftAuthorized(false);
    }

    public AccountBuilder withOverdraft() {
        return this.withOverdraftAuthorized(true);
    }

    public AccountBuilder withEnoughMoney(Withdraw command) {
        return this.withBalance(command.amount().value() + arbitraryAmount());
    }

    public Account build() {
        return new Account(balance, isOverdraftAuthorized, maxWithdrawal, withdraws);
    }
}
