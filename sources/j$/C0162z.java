package j$;

import j$.util.function.p;
import java.util.function.DoubleBinaryOperator;

/* renamed from: j$.z  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEz implements DoubleBinaryOperator {
    final /* synthetic */ p a;

    private /* synthetic */ CLASSNAMEz(p pVar) {
        this.a = pVar;
    }

    public static /* synthetic */ DoubleBinaryOperator a(p pVar) {
        if (pVar == null) {
            return null;
        }
        return pVar instanceof CLASSNAMEy ? ((CLASSNAMEy) pVar).a : new CLASSNAMEz(pVar);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
