package j$.wrappers;

import j$.util.function.i;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class P implements IntBinaryOperator {
    final /* synthetic */ i a;

    private /* synthetic */ P(i iVar) {
        this.a = iVar;
    }

    public static /* synthetic */ IntBinaryOperator a(i iVar) {
        if (iVar == null) {
            return null;
        }
        return iVar instanceof O ? ((O) iVar).a : new P(iVar);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}
