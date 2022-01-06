using System;
using System.Collections.Generic;
using System.Text;
using LanguageExt;
using PBTKata.Rentals;
using static LanguageExt.Prelude;

namespace PBTKata.Tests.Rentals.Solution
{
    public class NewRentalCalculator
    {
        public List<Rental> Rentals { get; init; }
        public bool Calculated { get { return calculated; } }
        public double Amount { get { return amount; } }

        private double amount = 0;
        private bool calculated = false;

        public NewRentalCalculator(List<Rental> rentals)
        {
            Rentals = rentals;
        }

        public Either<String, String> CalulateRental()
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

