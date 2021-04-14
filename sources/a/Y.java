package a;

import j$.util.function.y;
import java.util.function.IntPredicate;

public final /* synthetic */ class Y implements y {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IntPredicate var_a;

    private /* synthetic */ Y(IntPredicate intPredicate) {
        this.var_a = intPredicate;
    }

    public static /* synthetic */ y a(IntPredicate intPredicate) {
        if (intPredicate == null) {
            return null;
        }
        return intPredicate instanceof Z ? ((Z) intPredicate).var_a : new Y(intPredicate);
    }

    public /* synthetic */ boolean b(int i) {
        return this.var_a.test(i);
    }
}
