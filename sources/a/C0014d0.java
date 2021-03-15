package a;

import j$.util.function.z;
import java.util.function.IntToLongFunction;

/* renamed from: a.d0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd0 implements IntToLongFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ z var_a;

    private /* synthetic */ CLASSNAMEd0(z zVar) {
        this.var_a = zVar;
    }

    public static /* synthetic */ IntToLongFunction a(z zVar) {
        if (zVar == null) {
            return null;
        }
        return zVar instanceof CLASSNAMEc0 ? ((CLASSNAMEc0) zVar).var_a : new CLASSNAMEd0(zVar);
    }

    public /* synthetic */ long applyAsLong(int i) {
        return this.var_a.applyAsLong(i);
    }
}
