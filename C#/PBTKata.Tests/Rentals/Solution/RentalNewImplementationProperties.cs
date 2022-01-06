using FsCheck;
using FsCheck.Xunit;
using PBTKata.Rentals;
using System.Collections.Generic;
using System.Linq;

namespace PBTKata.Tests.Rentals.Solution
{
    public class RentalNewImplementationProperties
    {
        [Property]
        public Property NewImplementationShouldReturnTheSameResult(List<Rental> rentals)
            => (new RentalCalculator(rentals).CalculateRental() ==
                StatementPrinter.Print(rentals.ToSeq()))
            .ToProperty();
    }
}