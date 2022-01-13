package bank.solution;

import bank.Amount;
import bank.Withdraw;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.time.LocalDateGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.UUID;

public class WithdrawGenerator extends Generator<Withdraw> {
    private final LocalDateGenerator dateGenerator = new LocalDateGenerator();

    public WithdrawGenerator() {
        super(Withdraw.class);
    }

    @Override
    public Withdraw generate(SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
        return new Withdraw(
                UUID.randomUUID(),
                Amount.from(Math.abs(sourceOfRandomness.nextDouble())).get(),
                dateGenerator.generate(sourceOfRandomness, generationStatus));
    }
}
