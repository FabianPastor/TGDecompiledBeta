package j$;

import j$.util.function.L;
import java.util.function.LongPredicate;

public final /* synthetic */ class g0 implements LongPredicate {
    final /* synthetic */ L a;

    private /* synthetic */ g0(L l) {
        this.a = l;
    }

    public static /* synthetic */ LongPredicate a(L l) {
        if (l == null) {
            return null;
        }
        return l instanceof f0 ? ((f0) l).a : new g0(l);
    }

    public /* synthetic */ LongPredicate and(LongPredicate longPredicate) {
        return a(((f0) this.a).a(f0.b(longPredicate)));
    }

    public /* synthetic */ LongPredicate negate() {
        return a(((f0) this.a).c());
    }

    public /* synthetic */ LongPredicate or(LongPredicate longPredicate) {
        return a(((f0) this.a).d(f0.b(longPredicate)));
    }

    public /* synthetic */ boolean test(long j) {
        return ((f0) this.a).e(j);
    }
}
