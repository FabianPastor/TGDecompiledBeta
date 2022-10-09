package j$.wrappers;

import java.util.function.IntPredicate;
/* loaded from: classes2.dex */
public final /* synthetic */ class V {
    final /* synthetic */ IntPredicate a;

    private /* synthetic */ V(IntPredicate intPredicate) {
        this.a = intPredicate;
    }

    public static /* synthetic */ V a(IntPredicate intPredicate) {
        if (intPredicate == null) {
            return null;
        }
        return intPredicate instanceof W ? ((W) intPredicate).a : new V(intPredicate);
    }

    public boolean b(int i) {
        return this.a.test(i);
    }
}
