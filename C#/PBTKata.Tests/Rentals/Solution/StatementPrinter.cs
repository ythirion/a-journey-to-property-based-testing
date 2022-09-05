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
        public static Either<string, string> Print(Seq<Rental> rentals) =>
            rentals.IsEmpty ?
                Left("No rentals !!!") :
                Right(StatementFrom(rentals) + FormatTotal(rentals));

        private static string StatementFrom(Seq<Rental> rentals) =>
            rentals.Fold("", (statement, rental) => statement + FormatLine(rental));

        private static string FormatTotal(Seq<Rental> rentals) =>
            ToLine($"Total amount | {rentals.Sum(r => r.Amount):N2}");

        private static string FormatLine(Rental rental) =>
            ToLine($"{rental.Date} : {rental.Label} | {rental.Amount:N2}");

        private static string ToLine(string str) => $"{str}{Environment.NewLine}";
    }
}