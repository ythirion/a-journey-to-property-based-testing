using System;
using System.Diagnostics.Contracts;
using LanguageExt;

namespace PBTKata
{
    public static class OptionExtensions
    {
        public static T GetUnsafe<T>(this Option<T> opt) =>
            opt.IfNone(() => throw new InvalidOperationException("Can not Get value on None"));
    }

    public static class EitherExtensions
    {
        public static TRight RightValueUnsafe<TLeft, TRight>(this Either<TLeft, TRight> either) => either.RightToSeq().Single();
        public static TLeft LeftValueUnsafe<TLeft, TRight>(this Either<TLeft, TRight> either) => either.LeftToSeq().Single();
    }
}