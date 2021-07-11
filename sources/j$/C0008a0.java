package j$;

import j$.util.function.z;
import java.util.function.IntToLongFunction;

/* renamed from: j$.a0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa0 implements IntToLongFunction {
    final /* synthetic */ z a;

    private /* synthetic */ CLASSNAMEa0(z zVar) {
        this.a = zVar;
    }

    public static /* synthetic */ IntToLongFunction a(z zVar) {
        if (zVar == null) {
            return null;
        }
        return zVar instanceof Z ? ((Z) zVar).a : new CLASSNAMEa0(zVar);
    }

    public /* synthetic */ long applyAsLong(int i) {
        return this.a.applyAsLong(i);
    }
}
