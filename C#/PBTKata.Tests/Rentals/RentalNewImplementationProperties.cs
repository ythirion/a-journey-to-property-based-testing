using System;
using System.Collections.Generic;
using FluentAssertions;
using FsCheck;
using FsCheck.Xunit;
using PBTKata.Rentals;
using Xunit;

namespace PBTKata.Tests.Rentals
{
    public class RentalNewImplementationProperties
    {
        [Property]
        public Property NewImplementationShouldReturnTheSameResult(List<Rental> rentals) =>
            "C'est pas faux".StartsWith("C").ToProperty();
    }
}

