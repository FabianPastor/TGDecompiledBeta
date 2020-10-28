package j$;

import j$.util.function.t;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class T implements IntBinaryOperator {
    final /* synthetic */ t a;

    private /* synthetic */ T(t tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ IntBinaryOperator a(t tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof S ? ((S) tVar).a : new T(tVar);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}
