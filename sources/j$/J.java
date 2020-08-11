package j$;

import j$.util.function.D;
import java.util.function.IntPredicate;

public final /* synthetic */ class J implements D {
    final /* synthetic */ IntPredicate a;

    private /* synthetic */ J(IntPredicate intPredicate) {
        this.a = intPredicate;
    }

    public static /* synthetic */ D b(IntPredicate intPredicate) {
        if (intPredicate == null) {
            return null;
        }
        return intPredicate instanceof K ? ((K) intPredicate).a : new J(intPredicate);
    }

    public /* synthetic */ D a(D d) {
        return b(this.a.and(K.a(d)));
    }

    public /* synthetic */ D c() {
        return b(this.a.negate());
    }

    public /* synthetic */ D d(D d) {
        return b(this.a.or(K.a(d)));
    }

    public /* synthetic */ boolean e(int i) {
        return this.a.test(i);
    }
}
