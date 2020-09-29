package j$;

import j$.util.function.L;
import java.util.function.LongPredicate;

public final /* synthetic */ class f0 implements L {
    final /* synthetic */ LongPredicate a;

    private /* synthetic */ f0(LongPredicate longPredicate) {
        this.a = longPredicate;
    }

    public static /* synthetic */ L b(LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof g0 ? ((g0) longPredicate).a : new f0(longPredicate);
    }

    public /* synthetic */ L a(L l) {
        return b(this.a.and(g0.a(l)));
    }

    public /* synthetic */ L c() {
        return b(this.a.negate());
    }

    public /* synthetic */ L d(L l) {
        return b(this.a.or(g0.a(l)));
    }

    public /* synthetic */ boolean e(long j) {
        return this.a.test(j);
    }
}
