package j$.wrappers;

import java.util.function.DoubleFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class C implements j$.util.function.g {
    final /* synthetic */ DoubleFunction a;

    private /* synthetic */ C(DoubleFunction doubleFunction) {
        this.a = doubleFunction;
    }

    public static /* synthetic */ j$.util.function.g a(DoubleFunction doubleFunction) {
        if (doubleFunction == null) {
            return null;
        }
        return doubleFunction instanceof D ? ((D) doubleFunction).a : new C(doubleFunction);
    }

    @Override // j$.util.function.g
    public /* synthetic */ Object apply(double d) {
        return this.a.apply(d);
    }
}
