package j$.util.stream;

import j$.util.function.DoublePredicate;
import j$.util.function.Supplier;
import j$.util.stream.MatchOps;

public final /* synthetic */ class MatchOps$$ExternalSyntheticLambda0 implements Supplier {
    public final /* synthetic */ MatchOps.MatchKind f$0;
    public final /* synthetic */ DoublePredicate f$1;

    public /* synthetic */ MatchOps$$ExternalSyntheticLambda0(MatchOps.MatchKind matchKind, DoublePredicate doublePredicate) {
        this.f$0 = matchKind;
        this.f$1 = doublePredicate;
    }

    public final Object get() {
        return MatchOps.lambda$makeDouble$3(this.f$0, this.f$1);
    }
}
