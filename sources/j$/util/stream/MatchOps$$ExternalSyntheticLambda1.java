package j$.util.stream;

import j$.util.function.IntPredicate;
import j$.util.function.Supplier;
import j$.util.stream.MatchOps;

public final /* synthetic */ class MatchOps$$ExternalSyntheticLambda1 implements Supplier {
    public final /* synthetic */ MatchOps.MatchKind f$0;
    public final /* synthetic */ IntPredicate f$1;

    public /* synthetic */ MatchOps$$ExternalSyntheticLambda1(MatchOps.MatchKind matchKind, IntPredicate intPredicate) {
        this.f$0 = matchKind;
        this.f$1 = intPredicate;
    }

    public final Object get() {
        return MatchOps.lambda$makeInt$1(this.f$0, this.f$1);
    }
}
