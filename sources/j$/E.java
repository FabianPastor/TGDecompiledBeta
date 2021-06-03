package j$;

import j$.util.function.s;
import java.util.function.DoublePredicate;

public final /* synthetic */ class E implements s {
    final /* synthetic */ DoublePredicate a;

    private /* synthetic */ E(DoublePredicate doublePredicate) {
        this.a = doublePredicate;
    }

    public static /* synthetic */ s a(DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof F ? ((F) doublePredicate).a : new E(doublePredicate);
    }

    public /* synthetic */ boolean b(double d) {
        return this.a.test(d);
    }
}
