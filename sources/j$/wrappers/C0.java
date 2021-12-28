package j$.wrappers;

import j$.util.function.z;
import java.util.function.ToDoubleFunction;

public final /* synthetic */ class C0 implements ToDoubleFunction {
    final /* synthetic */ z a;

    private /* synthetic */ C0(z zVar) {
        this.a = zVar;
    }

    public static /* synthetic */ ToDoubleFunction a(z zVar) {
        if (zVar == null) {
            return null;
        }
        return zVar instanceof B0 ? ((B0) zVar).a : new C0(zVar);
    }

    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.a.applyAsDouble(obj);
    }
}
