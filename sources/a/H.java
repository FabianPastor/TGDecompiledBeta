package a;

import j$.util.function.s;
import java.util.function.DoublePredicate;

public final /* synthetic */ class H implements s {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DoublePredicate var_a;

    private /* synthetic */ H(DoublePredicate doublePredicate) {
        this.var_a = doublePredicate;
    }

    public static /* synthetic */ s a(DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof I ? ((I) doublePredicate).var_a : new H(doublePredicate);
    }

    public /* synthetic */ boolean b(double d) {
        return this.var_a.test(d);
    }
}
