package j$.wrappers;

import java.util.function.DoublePredicate;
/* loaded from: classes2.dex */
public final /* synthetic */ class E {
    final /* synthetic */ DoublePredicate a;

    private /* synthetic */ E(DoublePredicate doublePredicate) {
        this.a = doublePredicate;
    }

    public static /* synthetic */ E a(DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof F ? ((F) doublePredicate).a : new E(doublePredicate);
    }

    public boolean b(double d) {
        return this.a.test(d);
    }
}
