package bank;

import java.time.LocalDate;
import java.util.UUID;

public record Withdraw(
        UUID clientId,
        Amount amount,
        LocalDate requestDate) {

}