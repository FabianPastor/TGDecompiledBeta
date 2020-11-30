package j$;

import j$.util.function.s;
import java.util.function.DoublePredicate;

public final /* synthetic */ class H implements s {
    final /* synthetic */ DoublePredicate a;

    private /* synthetic */ H(DoublePredicate doublePredicate) {
        this.a = doublePredicate;
    }

    public static /* synthetic */ s a(DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof I ? ((I) doublePredicate).a : new H(doublePredicate);
    }

    public /* synthetic */ boolean b(double d) {
        return this.a.test(d);
    }
}
