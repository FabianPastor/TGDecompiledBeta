package j$;

import j$.util.function.z;
import java.util.function.LongFunction;

/* renamed from: j$.l0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl0 implements LongFunction {
    final /* synthetic */ z a;

    private /* synthetic */ CLASSNAMEl0(z zVar) {
        this.a = zVar;
    }

    public static /* synthetic */ LongFunction a(z zVar) {
        if (zVar == null) {
            return null;
        }
        return zVar instanceof CLASSNAMEk0 ? ((CLASSNAMEk0) zVar).a : new CLASSNAMEl0(zVar);
    }

    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
