using System;
using LanguageExt;
using static LanguageExt.Option<PBTKata.Post.PostalParcel>;

namespace PBTKata.Post
{
    public record PostalParcel
    {
        public const double MaxWeight = 20;
        public const double MinDeliveryCost = 1.99;
        public const double MaxDeliveryCost = 4.99;

        public double Weight { get; private init; }
        public static Option<PostalParcel> From(double weight) =>
            weight > 0 ? new PostalParcel { Weight = weight } : None;
    }

    public static class PostalParcelService
    {
        public static Option<double> CalculateDeliveryCosts(Option<PostalParcel> postalParcel) =>
            postalParcel.Map(p => p.Weight > PostalParcel.MaxWeight ? PostalParcel.MaxDeliveryCost : PostalParcel.MinDeliveryCost);
    }
}