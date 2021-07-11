package j$;

import j$.util.function.v;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class P implements IntBinaryOperator {
    final /* synthetic */ v a;

    private /* synthetic */ P(v vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ IntBinaryOperator a(v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof O ? ((O) vVar).a : new P(vVar);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}
