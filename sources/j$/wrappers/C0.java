package j$.wrappers;

import java.util.function.ToDoubleFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class C0 implements ToDoubleFunction {
    final /* synthetic */ j$.util.function.z a;

    private /* synthetic */ C0(j$.util.function.z zVar) {
        this.a = zVar;
    }

    public static /* synthetic */ ToDoubleFunction a(j$.util.function.z zVar) {
        if (zVar == null) {
            return null;
        }
        return zVar instanceof B0 ? ((B0) zVar).a : new C0(zVar);
    }

    @Override // java.util.function.ToDoubleFunction
    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.a.applyAsDouble(obj);
    }
}
