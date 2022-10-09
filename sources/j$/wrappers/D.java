package j$.wrappers;

import java.util.function.DoubleFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class D implements DoubleFunction {
    final /* synthetic */ j$.util.function.g a;

    private /* synthetic */ D(j$.util.function.g gVar) {
        this.a = gVar;
    }

    public static /* synthetic */ DoubleFunction a(j$.util.function.g gVar) {
        if (gVar == null) {
            return null;
        }
        return gVar instanceof C ? ((C) gVar).a : new D(gVar);
    }

    @Override // java.util.function.DoubleFunction
    public /* synthetic */ Object apply(double d) {
        return this.a.apply(d);
    }
}
