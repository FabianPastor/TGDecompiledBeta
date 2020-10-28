package j$;

import j$.util.function.s;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class M implements DoubleToLongFunction {
    final /* synthetic */ s a;

    private /* synthetic */ M(s sVar) {
        this.a = sVar;
    }

    public static /* synthetic */ DoubleToLongFunction a(s sVar) {
        if (sVar == null) {
            return null;
        }
        return sVar instanceof L ? ((L) sVar).a : new M(sVar);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
