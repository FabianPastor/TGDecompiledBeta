package j$.wrappers;

import j$.util.function.h;
import java.util.function.DoubleToLongFunction;

public final /* synthetic */ class J implements DoubleToLongFunction {
    final /* synthetic */ h a;

    private /* synthetic */ J(h hVar) {
        this.a = hVar;
    }

    public static /* synthetic */ DoubleToLongFunction a(h hVar) {
        if (hVar == null) {
            return null;
        }
        return hVar instanceof I ? ((I) hVar).a : new J(hVar);
    }

    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
