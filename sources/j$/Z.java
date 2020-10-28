package j$;

import java.util.function.IntPredicate;

public final /* synthetic */ class Z implements IntPredicate {
    final /* synthetic */ Y a;

    public static /* synthetic */ IntPredicate a(Y y) {
        if (y == null) {
            return null;
        }
        return y.a;
    }

    public IntPredicate and(IntPredicate intPredicate) {
        return a(Y.a(this.a.a.and(a(Y.a(intPredicate)))));
    }

    public IntPredicate negate() {
        return a(Y.a(this.a.a.negate()));
    }

    public IntPredicate or(IntPredicate intPredicate) {
        return a(Y.a(this.a.a.or(a(Y.a(intPredicate)))));
    }

    public boolean test(int i) {
        return this.a.a.test(i);
    }
}
