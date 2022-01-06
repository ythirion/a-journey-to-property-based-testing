using System;
using FsCheck;
using FsCheck.Xunit;
using LanguageExt;
using Xunit;
using static LanguageExt.Option<double>;
using static PBTKata.Post.PostalParcel;
using static PBTKata.Post.PostalParcelService;

namespace PBTKata.Tests.Post.Solution
{
    public class PostalParcelProperties
    {       
        [Fact]
        public void MaxDeliveryCostWhenWeightGreaterThanMaxWeight()
            => Prop.ForAll(Arb.Default.Float().Filter(x => x > MaxWeight),
                    weight => CalculateCost(weight) == MaxDeliveryCost)
                    .QuickCheckThrowOnFailure();

        [Fact]
        public void MinDeliveryCostWhenWeightLowerOrEqualsMaxWeight()
            => Prop.ForAll(Arb.Default.Float().Filter(x => x > 0 && x <= MaxWeight),
                    weight => CalculateCost(weight) == MinDeliveryCost)
                    .QuickCheckThrowOnFailure();

        [Fact]
        public void NoneWhenWeightLowerOrEquals0()
            => Prop.ForAll(Arb.Default.Float().Filter(x => x <= 0),
                    weight => CalculateCost(weight) == None)
                    .QuickCheckThrowOnFailure();

        private static Option<double> CalculateCost(double weight)
            => CalculateDeliveryCosts(From(weight));
    }
}