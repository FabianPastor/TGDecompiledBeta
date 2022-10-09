package j$.wrappers;

import java.util.function.DoubleToLongFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class I implements j$.util.function.h {
    final /* synthetic */ DoubleToLongFunction a;

    private /* synthetic */ I(DoubleToLongFunction doubleToLongFunction) {
        this.a = doubleToLongFunction;
    }

    public static /* synthetic */ j$.util.function.h a(DoubleToLongFunction doubleToLongFunction) {
        if (doubleToLongFunction == null) {
            return null;
        }
        return doubleToLongFunction instanceof J ? ((J) doubleToLongFunction).a : new I(doubleToLongFunction);
    }

    @Override // j$.util.function.h
    public /* synthetic */ long applyAsLong(double d) {
        return this.a.applyAsLong(d);
    }
}
