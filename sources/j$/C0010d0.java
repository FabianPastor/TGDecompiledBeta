package j$;

import j$.util.function.w;
import java.util.function.IntToLongFunction;

/* renamed from: j$.d0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd0 implements IntToLongFunction {
    final /* synthetic */ w a;

    private /* synthetic */ CLASSNAMEd0(w wVar) {
        this.a = wVar;
    }

    public static /* synthetic */ IntToLongFunction a(w wVar) {
        if (wVar == null) {
            return null;
        }
        return wVar instanceof CLASSNAMEc0 ? ((CLASSNAMEc0) wVar).a : new CLASSNAMEd0(wVar);
    }

    public /* synthetic */ long applyAsLong(int i) {
        return this.a.applyAsLong(i);
    }
}
