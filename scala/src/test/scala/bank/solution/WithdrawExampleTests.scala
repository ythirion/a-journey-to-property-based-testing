package bank.solution

import bank.solution.WithdrawExampleTests._
import bank.{Account, AccountService, Amount, Withdraw}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.{be, contain, have, startWith}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.Tables.Table
import org.scalatestplus.scalacheck.Checkers

import java.time.LocalDate
import java.util.UUID

class WithdrawExampleTests
    extends AnyFlatSpec
    with TableDrivenPropertyChecks
    with EitherValues {

  "withdraw" should "pass examples" in {
    forAll(`withdraw examples`) {
      (
          balance,
          isOverdraftAuthorized,
          maxWithdrawal,
          withdrawAmount,
          expectedResult
      ) =>
        val command = toWithdraw(withdrawAmount)
        val debitedAccount =
          AccountService.withdraw(
            Account(balance, isOverdraftAuthorized, maxWithdrawal),
            command
          )

        expectedResult match {
          case Left(expectedErrorMessage) =>
            debitedAccount.left.value should startWith(expectedErrorMessage)
          case Right(expectedBalance) =>
            debitedAccount.value.balance should be(expectedBalance)
            debitedAccount.value.withdraws should contain(command)
            debitedAccount.value.withdraws should have length 1
        }
    }
  }
}

object WithdrawExampleTests {
  private val `withdraw examples` =
    Table(
      (
        "balance",
        "isOverdraftAuthorized",
        "maxWithdrawal",
        "withdrawAmount",
        "expectedResult"
      ),
      (10_000, false, 1200, 1199.99, Right(8800.01)),
      (0, true, 500, 50d, Right(-50)),
      (1, false, 1200, 1199.99, Left("Insufficient balance to withdraw")),
      (0, false, 500, 50d, Left("Insufficient balance to withdraw")),
      (10_000, true, 1200, 1200.0001, Left("Amount exceeding your limit of")),
      (0, false, 500, 500d, Left("Amount exceeding your limit of"))
    )

  private def toWithdraw(amount: Double): Withdraw = {
    Withdraw(
      UUID.randomUUID(),
      Amount.from(amount).get,
      LocalDate.now()
    )
  }
}
