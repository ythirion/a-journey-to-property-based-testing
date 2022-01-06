using System;
using System.Text;
using LanguageExt;
using static LanguageExt.Prelude;

namespace PBTKata.Rentals
{
    public class RentalCalculator
    {
        public List<Rental> Rentals { get; init; }
        public bool Calculated { get { return calculated; } }
        public double Amount { get { return amount; } }

        private double amount = 0;
        private bool calculated = false;

        public RentalCalculator(List<Rental> rentals)
        {
            Rentals = rentals;
        }

        public Either<String, String> CalculateRental()
        {
            if(Rentals.IsNull() || Rentals.Count == 0)
            {
                return Left("No rentals !!!");
            }
            else
            {
                var result = new StringBuilder();

                foreach (var rental in Rentals)
                {
                    if(!calculated)
                    {
                        this.amount += rental.Amount;
                        result.AppendLine(FormatLine(rental, amount));
                    }
                }
                result.AppendLine($"Total amount | {this.amount:N2}");
                calculated = true;

                return Right(result.ToString());
            }
        }

        private static String FormatLine(Rental rental, Double amount) =>
            $"{rental.Date} : {rental.Label} | {rental.Amount:N2}";
    }
}

