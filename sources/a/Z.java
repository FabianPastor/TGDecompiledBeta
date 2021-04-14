package a;

import j$.util.function.y;
import java.util.function.IntPredicate;

public final /* synthetic */ class Z implements IntPredicate {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ y var_a;

    private /* synthetic */ Z(y yVar) {
        this.var_a = yVar;
    }

    public static /* synthetic */ IntPredicate a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof Y ? ((Y) yVar).var_a : new Z(yVar);
    }

    public /* synthetic */ IntPredicate and(IntPredicate intPredicate) {
        return a(Y.a(((Y) this.var_a).var_a.and(a(Y.a(intPredicate)))));
    }

    public /* synthetic */ IntPredicate negate() {
        return a(Y.a(((Y) this.var_a).var_a.negate()));
    }

    public /* synthetic */ IntPredicate or(IntPredicate intPredicate) {
        return a(Y.a(((Y) this.var_a).var_a.or(a(Y.a(intPredicate)))));
    }

    public /* synthetic */ boolean test(int i) {
        return ((Y) this.var_a).var_a.test(i);
    }
}
