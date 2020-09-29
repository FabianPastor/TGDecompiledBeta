package j$;

import j$.util.function.D;
import java.util.function.IntPredicate;

public final /* synthetic */ class V implements D {
    final /* synthetic */ IntPredicate a;

    private /* synthetic */ V(IntPredicate intPredicate) {
        this.a = intPredicate;
    }

    public static /* synthetic */ D b(IntPredicate intPredicate) {
        if (intPredicate == null) {
            return null;
        }
        return intPredicate instanceof W ? ((W) intPredicate).a : new V(intPredicate);
    }

    public /* synthetic */ D a(D d) {
        return b(this.a.and(W.a(d)));
    }

    public /* synthetic */ D c() {
        return b(this.a.negate());
    }

    public /* synthetic */ D d(D d) {
        return b(this.a.or(W.a(d)));
    }

    public /* synthetic */ boolean e(int i) {
        return this.a.test(i);
    }
}
