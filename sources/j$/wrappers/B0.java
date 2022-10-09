package j$.wrappers;

import java.util.function.ToDoubleFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class B0 implements j$.util.function.z {
    final /* synthetic */ ToDoubleFunction a;

    private /* synthetic */ B0(ToDoubleFunction toDoubleFunction) {
        this.a = toDoubleFunction;
    }

    public static /* synthetic */ j$.util.function.z a(ToDoubleFunction toDoubleFunction) {
        if (toDoubleFunction == null) {
            return null;
        }
        return toDoubleFunction instanceof C0 ? ((C0) toDoubleFunction).a : new B0(toDoubleFunction);
    }

    @Override // j$.util.function.z
    public /* synthetic */ double applyAsDouble(Object obj) {
        return this.a.applyAsDouble(obj);
    }
}
