namespace PBTKata.Bank
{
    public record Withdraw(Guid ClientId, Amount Amount, DateTime RequestDate);
}