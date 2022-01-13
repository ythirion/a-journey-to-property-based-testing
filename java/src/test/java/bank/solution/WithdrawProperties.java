package bank.solution;

import bank.Account;
import bank.AccountService;
import bank.Withdraw;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Either;
import lombok.val;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitQuickcheck.class)
public class WithdrawProperties {
    @Property
    public void withdraw_should_be_idem_potent(
            @From(WithdrawGenerator.class) Withdraw withdraw) {
        checkProperty(withdraw,
                accountBuilder -> accountBuilder
                        .withEnoughMoney(withdraw)
                        .withoutReachingMaxWithdrawal(withdraw),
                (account, debitedAccount) -> AccountService.withdraw(debitedAccount.get(), withdraw).get() == debitedAccount.get());
    }

    @Property
    public void balance_should_be_decremented_at_least_from_the_withdraw_amount(
            @From(WithdrawGenerator.class) Withdraw withdraw) {
        checkProperty(withdraw,
                accountBuilder -> accountBuilder
                        .withEnoughMoney(withdraw)
                        .withoutReachingMaxWithdrawal(withdraw),
                (account, result) -> result.get().balance() <= account.balance() - withdraw.amount().value());
    }

    @Property
    public void balance_should_be_decremented_at_least_from_the_withdraw_amount_when_insufficient_balance_but_overdraft_authorized(
            @From(WithdrawGenerator.class) Withdraw withdraw) {
        checkProperty(withdraw,
                accountBuilder -> accountBuilder
                        .withInsufficientBalance(withdraw)
                        .withoutReachingMaxWithdrawal(withdraw)
                        .withOverdraft(),
                (account, result) -> result.get().balance() <= account.balance() - withdraw.amount().value());
    }

    @Property
    public void withdraw_should_not_be_allowed_when_withdraw_amount_greater_than_maxWithdrawal(
            @From(WithdrawGenerator.class) Withdraw withdraw) {
        checkProperty(withdraw,
                accountBuilder -> accountBuilder.withdrawAmountReachingMaxWithdrawal(withdraw),
                (account, result) -> result.getLeft().startsWith("Amount exceeding your limit of"));
    }

    @Property
    public void withdraw_should_not_be_allowed_when_insufficient_balance_and_no_overdraft(
            @From(WithdrawGenerator.class) Withdraw withdraw) {
        checkProperty(withdraw,
                accountBuilder -> accountBuilder
                        .withInsufficientBalance(withdraw)
                        .withoutOverdraft()
                        .withoutReachingMaxWithdrawal(withdraw),
                (account, result) -> result.getLeft().startsWith("Insufficient balance to withdraw"));
    }

    private void checkProperty(
            Withdraw withdraw,
            Function1<AccountBuilder, AccountBuilder> accountConfiguration,
            Function2<Account, Either<String, Account>, Boolean> property) {
        val account = accountConfiguration.apply(AccountBuilder.newAccount()).build();
        assertThat(property.apply(account, AccountService.withdraw(account, withdraw))).isTrue();
    }
}