package j$;

import j$.util.function.L;
import java.util.function.LongPredicate;

public final /* synthetic */ class V implements LongPredicate {
    final /* synthetic */ L a;

    private /* synthetic */ V(L l) {
        this.a = l;
    }

    public static /* synthetic */ LongPredicate a(L l) {
        if (l == null) {
            return null;
        }
        return l instanceof U ? ((U) l).a : new V(l);
    }

    public /* synthetic */ LongPredicate and(LongPredicate longPredicate) {
        return a(((U) this.a).a(U.b(longPredicate)));
    }

    public /* synthetic */ LongPredicate negate() {
        return a(((U) this.a).c());
    }

    public /* synthetic */ LongPredicate or(LongPredicate longPredicate) {
        return a(((U) this.a).d(U.b(longPredicate)));
    }

    public /* synthetic */ boolean test(long j) {
        return ((U) this.a).e(j);
    }
}
