package j$;

import j$.util.function.v;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class T implements IntBinaryOperator {
    final /* synthetic */ v a;

    private /* synthetic */ T(v vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ IntBinaryOperator a(v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof S ? ((S) vVar).a : new T(vVar);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}
