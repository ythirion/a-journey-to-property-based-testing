using System;
using System.Collections.Generic;
using System.Text;
using LanguageExt;
using PBTKata.Rentals;
using static LanguageExt.Prelude;

namespace PBTKata.Tests.Rentals.Solution
{
    public static class StatementPrinter
    {
        public static Either<String, String> Print(Seq<Rental> rentals) =>
            rentals.IsEmpty ?
                Left("No rentals !!!") :
                Right(StatementFrom(rentals) + FormatTotal(rentals));

        private static String StatementFrom(Seq<Rental> rentals) =>
            rentals.Fold("", (statement, rental) => statement + FormatLine(rental));

        private static String FormatTotal(Seq<Rental> rentals) =>
            ToLine($"Total amount | {rentals.Sum(r => r.Amount):N2}");

        private static String FormatLine(Rental rental) =>
            ToLine($"{rental.Date} : {rental.Label} | {rental.Amount:N2}");

        private static String ToLine(String str) => $"{str}{Environment.NewLine}";
    }
}