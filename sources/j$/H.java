package j$;

import java.util.function.DoublePredicate;

public final /* synthetic */ class H {
    final /* synthetic */ DoublePredicate a;

    private /* synthetic */ H(DoublePredicate doublePredicate) {
        this.a = doublePredicate;
    }

    public static /* synthetic */ H a(DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof I ? ((I) doublePredicate).a : new H(doublePredicate);
    }

    public boolean b(double d) {
        return this.a.test(d);
    }
}
