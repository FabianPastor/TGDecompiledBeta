package j$;

import j$.util.function.L;
import java.util.function.LongPredicate;

public final /* synthetic */ class U implements L {
    final /* synthetic */ LongPredicate a;

    private /* synthetic */ U(LongPredicate longPredicate) {
        this.a = longPredicate;
    }

    public static /* synthetic */ L b(LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof V ? ((V) longPredicate).a : new U(longPredicate);
    }

    public /* synthetic */ L a(L l) {
        return b(this.a.and(V.a(l)));
    }

    public /* synthetic */ L c() {
        return b(this.a.negate());
    }

    public /* synthetic */ L d(L l) {
        return b(this.a.or(V.a(l)));
    }

    public /* synthetic */ boolean e(long j) {
        return this.a.test(j);
    }
}
