package j$;

import java.util.function.IntPredicate;

public final /* synthetic */ class Y {
    final /* synthetic */ IntPredicate a;

    private /* synthetic */ Y(IntPredicate intPredicate) {
        this.a = intPredicate;
    }

    public static /* synthetic */ Y a(IntPredicate intPredicate) {
        if (intPredicate == null) {
            return null;
        }
        return intPredicate instanceof Z ? ((Z) intPredicate).a : new Y(intPredicate);
    }

    public boolean b(int i) {
        return this.a.test(i);
    }
}
