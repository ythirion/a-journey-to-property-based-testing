using LanguageExt;

namespace PBTKata.Bank
{
    public record Amount
    {
        public decimal Value { get; private init; }
        public static Option<Amount> From(decimal amount) =>
            amount > 0 ? new Amount { Value = amount } : Option<Amount>.None;

        public static bool operator >=(Amount a, Amount b) => a.Value >= b.Value;
        public static bool operator <=(Amount a, Amount b) => a.Value <= b.Value;
        public static bool operator >(Amount a, Amount b) => a.Value > b.Value;
        public static bool operator <(Amount a, Amount b) => a.Value < b.Value;
        public static Amount operator -(Amount a, Amount b) => a with { Value = a.Value - b.Value };
        public static Amount operator +(Amount a, Amount b) => a with { Value = a.Value + b.Value };
    }

    public static class AmountExtensions
    {
        public static Amount ToAmountUnsafe(this decimal amount) => Amount.From(amount).GetUnsafe();
    }
}