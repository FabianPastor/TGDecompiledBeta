package j$;

import j$.util.function.y;
import java.util.function.IntPredicate;

public final /* synthetic */ class V implements y {
    final /* synthetic */ IntPredicate a;

    private /* synthetic */ V(IntPredicate intPredicate) {
        this.a = intPredicate;
    }

    public static /* synthetic */ y a(IntPredicate intPredicate) {
        if (intPredicate == null) {
            return null;
        }
        return intPredicate instanceof W ? ((W) intPredicate).a : new V(intPredicate);
    }

    public /* synthetic */ boolean b(int i) {
        return this.a.test(i);
    }
}
