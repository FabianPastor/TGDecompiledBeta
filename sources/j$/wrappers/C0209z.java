package j$.wrappers;

import j$.util.function.d;
import java.util.function.DoubleBinaryOperator;

/* renamed from: j$.wrappers.z  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEz implements DoubleBinaryOperator {
    final /* synthetic */ d a;

    private /* synthetic */ CLASSNAMEz(d dVar) {
        this.a = dVar;
    }

    public static /* synthetic */ DoubleBinaryOperator a(d dVar) {
        if (dVar == null) {
            return null;
        }
        return dVar instanceof CLASSNAMEy ? ((CLASSNAMEy) dVar).a : new CLASSNAMEz(dVar);
    }

    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
