package j$.wrappers;

import j$.util.function.j;
import java.util.function.IntBinaryOperator;

public final /* synthetic */ class P implements IntBinaryOperator {
    final /* synthetic */ j a;

    private /* synthetic */ P(j jVar) {
        this.a = jVar;
    }

    public static /* synthetic */ IntBinaryOperator a(j jVar) {
        if (jVar == null) {
            return null;
        }
        return jVar instanceof O ? ((O) jVar).a : new P(jVar);
    }

    public /* synthetic */ int applyAsInt(int i, int i2) {
        return this.a.applyAsInt(i, i2);
    }
}
