package j$;

import j$.util.function.z;
import java.util.function.IntToLongFunction;

/* renamed from: j$.d0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd0 implements IntToLongFunction {
    final /* synthetic */ z a;

    private /* synthetic */ CLASSNAMEd0(z zVar) {
        this.a = zVar;
    }

    public static /* synthetic */ IntToLongFunction a(z zVar) {
        if (zVar == null) {
            return null;
        }
        return zVar instanceof CLASSNAMEc0 ? ((CLASSNAMEc0) zVar).a : new CLASSNAMEd0(zVar);
    }

    public /* synthetic */ long applyAsLong(int i) {
        return this.a.applyAsLong(i);
    }
}
