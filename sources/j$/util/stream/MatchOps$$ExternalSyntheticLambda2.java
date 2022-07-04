package j$.util.stream;

import j$.util.function.LongPredicate;
import j$.util.function.Supplier;
import j$.util.stream.MatchOps;

public final /* synthetic */ class MatchOps$$ExternalSyntheticLambda2 implements Supplier {
    public final /* synthetic */ MatchOps.MatchKind f$0;
    public final /* synthetic */ LongPredicate f$1;

    public /* synthetic */ MatchOps$$ExternalSyntheticLambda2(MatchOps.MatchKind matchKind, LongPredicate longPredicate) {
        this.f$0 = matchKind;
        this.f$1 = longPredicate;
    }

    public final Object get() {
        return MatchOps.lambda$makeLong$2(this.f$0, this.f$1);
    }
}
