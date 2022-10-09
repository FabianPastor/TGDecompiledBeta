package j$.wrappers;

import java.util.function.IntToDoubleFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class X {
    final /* synthetic */ IntToDoubleFunction a;

    private /* synthetic */ X(IntToDoubleFunction intToDoubleFunction) {
        this.a = intToDoubleFunction;
    }

    public static /* synthetic */ X b(IntToDoubleFunction intToDoubleFunction) {
        if (intToDoubleFunction == null) {
            return null;
        }
        return intToDoubleFunction instanceof Y ? ((Y) intToDoubleFunction).a : new X(intToDoubleFunction);
    }

    public double a(int i) {
        return this.a.applyAsDouble(i);
    }
}
