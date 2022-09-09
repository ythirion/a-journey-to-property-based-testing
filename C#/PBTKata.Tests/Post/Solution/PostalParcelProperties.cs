using FsCheck;
using LanguageExt;
using Xunit;
using static FsCheck.Arb.Default;
using static FsCheck.Prop;
using static LanguageExt.Option<double>;
using static PBTKata.Post.PostalParcel;
using static PBTKata.Post.PostalParcelService;

namespace PBTKata.Tests.Post.Solution
{
    public class PostalParcelProperties
    {
        [Fact]
        public void MaxDeliveryCostWhenWeightGreaterThanMaxWeight()
            => ForAll(Float().Filter(x => x > MaxWeight),
                    weight => CalculateCost(weight) == MaxDeliveryCost)
                .QuickCheckThrowOnFailure();

        [Fact]
        public void MinDeliveryCostWhenWeightLowerOrEqualsMaxWeight()
            => ForAll(Float().Filter(x => x > 0 && x <= MaxWeight),
                    weight => CalculateCost(weight) == MinDeliveryCost)
                .QuickCheckThrowOnFailure();

        [Fact]
        public void NoneWhenWeightLowerOrEquals0()
            => ForAll(Float().Filter(x => x <= 0),
                    weight => CalculateCost(weight) == None)
                .QuickCheckThrowOnFailure();

        private static Option<double> CalculateCost(double weight)
            => CalculateDeliveryCosts(From(weight));
    }
}