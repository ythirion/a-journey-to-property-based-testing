package bank.solution;

import bank.Account;
import bank.AccountService;
import bank.Amount;
import bank.Withdraw;
import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.assertj.core.api.Assertions.assertThat;

class WithdrawExampleTests {
    public static Stream<Arguments> examples() {
        return Stream.of(
                Arguments.of(10_000, false, 1200, 1199.99, right(8800.01)),
                Arguments.of(0, true, 500, 50, right(-50d)),
                Arguments.of(1, false, 1200, 1199.99, left("Insufficient balance to withdraw")),
                Arguments.of(0, false, 500, 50, left("Insufficient balance to withdraw")),
                Arguments.of(10_000, true, 1200, 1200.0001, left("Amount exceeding your limit of")),
                Arguments.of(0, false, 500, 500, left("Amount exceeding your limit of"))
        );
    }

    @ParameterizedTest
    @MethodSource("examples")
    void withdraw_should(
            double balance,
            boolean isOverdraftAuthorized,
            double maxWithdrawal,
            double withdrawAmount,
            Either<String, Double> expectedResult
    ) {
        val command = new Withdraw(UUID.randomUUID(), Amount.from(withdrawAmount).get(), LocalDate.now());
        val account = new Account(balance, isOverdraftAuthorized, maxWithdrawal, List.empty());
        val result = AccountService.withdraw(account, command);

        expectedResult
                .peek(expectedBalance -> {
                    val debitedAccount = result.get();

                    assertThat(debitedAccount.balance()).isEqualTo(expectedBalance);
                    assertThat(debitedAccount.withdraws())
                            .contains(command)
                            .hasSize(1);
                })
                .peekLeft(expectedErrorMessage -> assertThat(result.getLeft()).startsWith(expectedErrorMessage));
    }
}
