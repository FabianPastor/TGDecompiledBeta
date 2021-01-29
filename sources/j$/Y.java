package j$;

import j$.util.function.y;
import java.util.function.IntPredicate;

public final /* synthetic */ class Y implements y {
    final /* synthetic */ IntPredicate a;

    private /* synthetic */ Y(IntPredicate intPredicate) {
        this.a = intPredicate;
    }

    public static /* synthetic */ y a(IntPredicate intPredicate) {
        if (intPredicate == null) {
            return null;
        }
        return intPredicate instanceof Z ? ((Z) intPredicate).a : new Y(intPredicate);
    }

    public /* synthetic */ boolean b(int i) {
        return this.a.test(i);
    }
}
